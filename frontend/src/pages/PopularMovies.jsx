import React, { useEffect, useState } from 'react'
import { getPopularMovies } from '../api/kinopoisk'
import MovieModal from '../components/MovieModal'

export default function PopularMovies() {
  const [movies, setMovies] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [selectedId, setSelectedId] = useState(null)

  useEffect(() => {
    let mounted = true
    setLoading(true)
    getPopularMovies()
      .then((data) => {
        if (!mounted) return
        setMovies(data.results || [])
        setError(null)
      })
      .catch((err) => {
        if (!mounted) return
        setError(err.message || 'Ошибка при загрузке')
      })
      .finally(() => {
        if (!mounted) return
        setLoading(false)
      })

    return () => {
      mounted = false
    }
  }, [])

  if (loading) return <div className="status">Загрузка популярных фильмов...</div>
  if (error) return <div className="status error">Ошибка: {error}</div>

  return (
    <section className="movies">
      {movies.length === 0 ? (
        <div className="status">Фильмы не найдены</div>
      ) : (
        <div className="grid">
          {movies.map((m) => (
            <article key={m.id} className="card" onClick={() => setSelectedId(m.id)}>
              {(m.poster_path || m.posterUrl) && (m.poster_path || m.posterUrl).startsWith('http') ? (
                <img
                  src={(m.poster_path || m.posterUrl)}
                  alt={m.title || m.nameRu || m.nameOriginal}
                  className="poster"
                />
              ) : (
                <div className="no-poster">Нет постера</div>
              )}

              <div className="meta">
                <h3 className="title">{m.title || m.nameRu || m.nameOriginal}</h3>
                <div className="sub">
                  <span>{m.release_date ? (m.release_date || '').toString().slice(0,4) : (m.year || '')}</span>
                  <span>★ {m.vote_average || (m.rating && m.rating.kp) || ''}</span>
                </div>
              </div>
            </article>
          ))}
        </div>
      )}

      {selectedId && (
        <MovieModal movieId={selectedId} onClose={() => setSelectedId(null)} />
      )}
    </section>
  )
}
