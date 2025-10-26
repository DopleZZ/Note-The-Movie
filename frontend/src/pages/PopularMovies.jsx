import React, { useEffect, useState } from 'react'
import { getPopularMovies } from '../api/tmdb'

export default function PopularMovies() {
  const [movies, setMovies] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

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
            <article key={m.id} className="card">
              {m.poster_path ? (
                <img
                  src={`https://image.tmdb.org/t/p/w300${m.poster_path}`}
                  alt={m.title}
                  className="poster"
                />
              ) : (
                <div className="no-poster">Нет постера</div>
              )}

              <div className="meta">
                <h3 className="title">{m.title}</h3>
                <div className="sub">
                  <span>{m.release_date}</span>
                  <span>★ {m.vote_average}</span>
                </div>
              </div>
            </article>
          ))}
        </div>
      )}
    </section>
  )
}
