export interface Repo {
  id: number,
  status: string,
  repo: {
    name: string,
    url: string,
  },
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
