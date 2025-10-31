const BASE = import.meta.env.VITE_API_BASE || ''

function getAuthHeaders() {
  const token = localStorage.getItem('ntm_token')
  return token ? { Authorization: `Bearer ${token}` } : {}
}

export async function getPopularMovies(page = 1) {
  const url = `${BASE}/api/tmdb/popular?page=${page}`
  const headers = getAuthHeaders()
  const res = await fetch(url, { headers })
  if (!res.ok) {
    const text = await res.text()
    throw new Error(`Error: ${res.status} ${text}`)
  }
  return res.json()
}

export async function getMovieDetails(id) {
  const url = `${BASE}/api/tmdb/movie/${id}`
  const headers = getAuthHeaders()
  const res = await fetch(url, { headers })
  if (!res.ok) {
    const text = await res.text()
    throw new Error(`Error: ${res.status} ${text}`)
  }
  return res.json()
}

export async function searchMovies(query, page = 1) {
  const url = `${BASE}/api/tmdb/search?query=${encodeURIComponent(query)}&page=${page}`
  const headers = getAuthHeaders()
  const res = await fetch(url, { headers })
  if (!res.ok) {
    const text = await res.text()
    throw new Error(`Error: ${res.status} ${text}`)
  }
  return res.json()
}
