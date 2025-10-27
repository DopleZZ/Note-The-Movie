const BASE = import.meta.env.VITE_API_BASE || ''

function saveAuth(token, username) {
  localStorage.setItem('ntm_token', token)
  localStorage.setItem('ntm_user', username)
}

export function getToken() {
  return localStorage.getItem('ntm_token')
}

export function getCurrentUser() {
  return localStorage.getItem('ntm_user')
}

export function logout() {
  localStorage.removeItem('ntm_token')
  localStorage.removeItem('ntm_user')
}

export async function login(username, password) {
  const res = await fetch(`${BASE}/api/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  })

  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || 'Login failed')
  }

  const data = await res.json()
  saveAuth(data.token, data.username)
  return data
}

export async function register(username, password) {
  const res = await fetch(`${BASE}/api/auth/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  })

  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || 'Register failed')
  }

  const data = await res.json()
  saveAuth(data.token, data.username)
  return data
}
