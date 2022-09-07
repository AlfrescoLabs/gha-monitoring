export interface Repo {
  id: number,
  status: string,
  status_icon: string,
  conclusion: string,
  statusColor: string,
  updatedAt: string,
  repo: {
    name: string,
    url: string,
  },
  headBranch: {
    name: string,
    url: string,
  }
  commit: {
    id: string,
    url: string,
  },
  run: {
    number: number,
    url: string,
  },
  owner: {
    name: string,
    url: string,
  }
}
