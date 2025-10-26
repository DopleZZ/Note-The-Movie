const BASE = 'https://api.themoviedb.org/3'
import { getToken } from './auth'

function getApiKey() {
  const key = import.meta.env.VITE_TMDB_API_KEY
  if (!key) {
    throw new Error('TMDB API key not found. Add VITE_TMDB_API_KEY to your .env file')
  }
  return key
}

export async function getPopularMovies(page = 1) {
  const apiKey = getApiKey()
  const url = `${BASE}/movie/popular?api_key=${apiKey}&language=ru-RU&page=${page}`
  const headers = {}
  const token = getToken()
  if (token) headers['Authorization'] = `Bearer ${token}`
  const res = await fetch(url, { headers })
  if (!res.ok) {
    const text = await res.text()
    throw new Error(`TMDB error: ${res.status} ${text}`)
  }
  return res.json()
}

export async function getMovieDetails(id) {
  const apiKey = getApiKey()
  const url = `${BASE}/movie/${id}?api_key=${apiKey}&language=ru-RU&append_to_response=credits`
  const headers = {}
  const token = getToken()
  if (token) headers['Authorization'] = `Bearer ${token}`
  const res = await fetch(url, { headers })
  if (!res.ok) {
    const text = await res.text()
    throw new Error(`TMDB error: ${res.status} ${text}`)
  }
  return res.json()
}

export async function searchMovies(query, page = 1) {
  const apiKey = getApiKey()
  const url = `${BASE}/search/movie?api_key=${apiKey}&language=ru-RU&query=${encodeURIComponent(query)}&page=${page}`
  const headers = {}
  const token = getToken()
  if (token) headers['Authorization'] = `Bearer ${token}`
  const res = await fetch(url, { headers })
  if (!res.ok) {
    const text = await res.text()
    throw new Error(`TMDB error: ${res.status} ${text}`)
  }
  return res.json()
}
