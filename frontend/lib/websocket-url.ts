export function toWebSocketUrl(apiUrl: string, path: string) {
  return apiUrl.replace(/^http/, "ws") + path;
}
