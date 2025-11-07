const BASE = import.meta.env.VITE_API_BASE || ''

function getAuthHeaders() {
  const token = localStorage.getItem('ntm_token')
  return token ? { Authorization: `Bearer ${token}` } : {}
}

export async function getPopularMovies(page = 1) {
  const url = `${BASE}/api/kinopoisk/popular?page=${page}`
  const headers = getAuthHeaders()
  const res = await fetch(url, { headers })
  if (!res.ok) {
    const text = await res.text()
    throw new Error(`Error: ${res.status} ${text}`)
  }
  const data = await res.json()
  // normalize results to a consistent shape frontend expects
  if (data && Array.isArray(data.results)) {
    data.results = data.results.map(normalizeFilm)
  }
  return data
}

export async function getMovieDetails(id) {
  const url = `${BASE}/api/kinopoisk/movie/${id}`
  const headers = getAuthHeaders()
  const res = await fetch(url, { headers })
  if (!res.ok) {
    const text = await res.text()
    throw new Error(`Error: ${res.status} ${text}`)
  }
  const data = await res.json()
  return normalizeFilm(data)
}

export async function searchMovies(query, page = 1) {
  const url = `${BASE}/api/kinopoisk/search?query=${encodeURIComponent(query)}&page=${page}`
  const headers = getAuthHeaders()
  const res = await fetch(url, { headers })
  if (!res.ok) {
    const text = await res.text()
    throw new Error(`Error: ${res.status} ${text}`)
  }
  const data = await res.json()
  if (data && Array.isArray(data.results)) data.results = data.results.map(normalizeFilm)
  return data
}

function normalizeFilm(f) {
  if (!f) return f
  const out = { ...f }
  if (!out.id) out.id = out.filmId || out.kinopoiskId || out.movieId
  if (!out.title) out.title = out.title || out.nameRu || out.nameEn || out.nameOriginal
  if (!out.poster_path) out.poster_path = out.poster_path || out.posterUrl || out.posterUrlPreview || out.poster_preview
  if (!out.release_date) out.release_date = out.release_date || out.year || out.releaseDate
  if (!out.overview) out.overview = out.overview || out.description || out.shortDescription
  if (!out.vote_average && out.vote_average !== 0) out.vote_average = out.vote_average || (out.rating && out.rating.kp) || out.rating
  // normalize genres to [{name: '...'}]
  if (out.genres && Array.isArray(out.genres)) {
    out.genres = out.genres.map(g => (typeof g === 'string' ? { name: g } : (g.name ? g : { name: g.genre || '' })))
  }
  return out
}
