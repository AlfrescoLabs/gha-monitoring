# Documentation

All guides that contribute to improving the GitHub Actions monitoring & observability experience can be found here.

## Remote debugging with SSH

GitHub doesn't provide any native support for SSH debug access to builds, but there are community actions providing that feature levering external services like upterm and tmate (like travis).

Most used one is [mxschmitt/action-tmate](https://github.com/mxschmitt/action-tmate) with 1.6K stars.

To debug a build is necessary to add on-demand a step like:

```yml
    - name: Setup tmate session
      uses: mxschmitt/action-tmate@v3
      with:
        # provide access to SSH user that triggered the build
        limit-access-to-actor: true
```

But you can also run the step on-demand with a manual build via `workflow_dispatch` event, see [example](../.github/workflows/test-tmate.yml).

When executing that step, the job will block. If you want to continue with the following steps, just create a file named `continue` in the current workspace folder:

```sh
touch continue
```

Please be aware that when the last command of the job finish, also the tmate session will be terminated automatically.

## WORM Audit with GH Actions

### Setting up a S3 WORM bucket and an IAM user

Many of our builds already publish artifacts to S3 (to the Staging and Downloads buckets) but typically the credentials are for a user in the customer-facing AWS account. Since the audit is expected to be an internal feature then we have used the Engineering account to create a bucket.

When creating the bucket then ensure that:

* Versioning is enabled
* Object Lock is enabled

After creating the bucket then edit the properties to set:

* Default retention to enabled
* Retention mode to Compliance
* Default retention period to some suitable period (e.g. a release typically hits end of maintenance after 3 years)

> :warning: Once compliance mode is selected then no one will be able to delete artifacts uploaded to the bucket.

> :memo: If you try to delete an artifact then it may seem to be successful, but it is actually just hidden by default. It can still be viewed by selecting "Show versions".

We will need a IAM user who is able to publish to this bucket, so create a new IAM user with an access key. Attach an existing policy to the user "AmazonS3FullAccess". Make a note of the key id and secret access key as they need to be stored in GitHub Secrets.

### Using GitHub Actions to publish audit reports

We assume that the workflow will be split into several jobs, and a subset of these jobs will create reports that we want to publish to the audit bucket.

The following steps create a filename unique to the execution of the workflow, fills it with a report and then publishes it as an artifact on the build:

```yml
      - name: create report filename
        run: |
          branch_name=$(echo ${{ github.ref_name }} | sed "s|/|_|g")
          echo "REPORT_FILE=report-${{ github.event.repository.name }}-${branch_name}-$(date +%s).json" >> ${GITHUB_ENV}

      - name: create report
        run: |
          mkdir reports
          echo "REPORT GOES HERE" > reports/${{ env.REPORT_FILE }}

      - uses: actions/upload-artifact@v3
        with:
          name: reportA
          path: reports/${{ env.REPORT_FILE }}
```

A second job can then download the artifacts and publish them to S3.

```yml
    - uses: actions/download-artifact@v2
      with:
        name: reportA
        path: reports

    - name: Upload report to S3 bucket
      uses: tpaschalis/s3-sync-action@master
      with:
        args: --acl private
      env:
        FILE: reports/*
        AWS_REGION: 'eu-west-1'
        AWS_S3_BUCKET: ${{ secrets.AUDIT_AWS_S3_BUCKET }}
        AWS_ACCESS_KEY_ID: ${{ secrets.AUDIT_AWS_ACCESS_KEY_ID }}
        AWS_SECRET_ACCESS_KEY: ${{ secrets.AUDIT_AWS_SECRET_ACCESS_KEY }}
```

We need to create GitHub repository secrets for the variables `AUDIT_AWS_S3_BUCKET`, `AUDIT_AWS_ACCESS_KEY_ID` and `AUDIT_AWS_SECRET_ACCESS_KEY`.

> :memo: It would be even better to create these as organisation secrets, so that they can be reused across all builds.
