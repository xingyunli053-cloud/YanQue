const TOKEN_KEY = 'yanque_token'
const SIGN_SECRET_KEY = 'yanque_sign_secret'

export const getToken = () => localStorage.getItem(TOKEN_KEY) || ''
export const getSignSecret = () => localStorage.getItem(SIGN_SECRET_KEY) || ''

export function saveSession({ token, signSecret }) {
  localStorage.setItem(TOKEN_KEY, token)
  localStorage.setItem(SIGN_SECRET_KEY, signSecret)
}

export function clearSession() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(SIGN_SECRET_KEY)
}

export const isAuthenticated = () => Boolean(getToken())
