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
