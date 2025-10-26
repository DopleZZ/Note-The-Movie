const BASE = 'https://api.themoviedb.org/3'

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
  const res = await fetch(url)
  if (!res.ok) {
    const text = await res.text()
    throw new Error(`TMDB error: ${res.status} ${text}`)
  }
  return res.json()
}
