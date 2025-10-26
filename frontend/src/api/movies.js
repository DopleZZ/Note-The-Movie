const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080'

function getAuthHeaders() {
  const token = localStorage.getItem('ntm_token')
  return token ? { Authorization: `Bearer ${token}` } : {}
}

function handleAuthError(response) {
  if (response.status === 401 || response.status === 403) {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    window.location.reload()
  }
  return response
}

export async function addToWatched(movie) {
  const response = await fetch(`${API_BASE}/api/movies/watched`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...getAuthHeaders()
    },
    body: JSON.stringify({
      movieId: movie.id,
      title: movie.title,
      posterPath: movie.poster_path,
      overview: movie.overview,
      releaseDate: movie.release_date
    })
  })
  handleAuthError(response)
  if (!response.ok) throw new Error('Failed to add to watched')
  return response.text()
}

export async function removeFromWatched(movieId) {
  const response = await fetch(`${API_BASE}/api/movies/watched/${movieId}`, {
    method: 'DELETE',
    headers: getAuthHeaders()
  })
  handleAuthError(response)
  if (!response.ok) throw new Error('Failed to remove from watched')
  return response.text()
}

export async function getWatched() {
  const response = await fetch(`${API_BASE}/api/movies/watched`, {
    headers: getAuthHeaders()
  })
  handleAuthError(response)
  if (!response.ok) throw new Error('Failed to get watched movies')
  return response.json()
}

export async function addToFavorites(movie) {
  const response = await fetch(`${API_BASE}/api/movies/favorites`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...getAuthHeaders()
    },
    body: JSON.stringify({
      movieId: movie.id,
      title: movie.title,
      posterPath: movie.poster_path,
      overview: movie.overview,
      releaseDate: movie.release_date
    })
  })
  handleAuthError(response)
  if (!response.ok) throw new Error('Failed to add to favorites')
  return response.text()
}

export async function removeFromFavorites(movieId) {
  const response = await fetch(`${API_BASE}/api/movies/favorites/${movieId}`, {
    method: 'DELETE',
    headers: getAuthHeaders()
  })
  handleAuthError(response)
  if (!response.ok) throw new Error('Failed to remove from favorites')
  return response.text()
}

export async function getFavorites() {
  const response = await fetch(`${API_BASE}/api/movies/favorites`, {
    headers: getAuthHeaders()
  })
  handleAuthError(response)
  if (!response.ok) throw new Error('Failed to get favorite movies')
  return response.json()
}