import React, { useEffect, useState } from 'react'
import { getWatched, getFavorites } from '../api/movies'
import MovieModal from '../components/MovieModal'

export default function Profile({ user, onLogout }) {
  const [watched, setWatched] = useState([])
  const [favorites, setFavorites] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [selectedId, setSelectedId] = useState(null)

  useEffect(() => {
    async function loadLists() {
      try {
        const [w, f] = await Promise.all([getWatched(), getFavorites()])
        setWatched(w)
        setFavorites(f)
      } catch (err) {
        setError(err.message)
      } finally {
        setLoading(false)
      }
    }
    loadLists()
  }, [])

  return (
    <section style={{padding:20}}>
      <h2>Профиль пользователя</h2>
      <div style={{marginTop:12}}>
        <strong>Имя пользователя:</strong>
        <div style={{marginTop:6}}>{user || '—'}</div>
      </div>

      {loading ? (
        <div>Загрузка списков...</div>
      ) : error ? (
        <div style={{color:'red'}}>Ошибка: {error}</div>
      ) : (
        <>
          <div style={{marginTop:20}}>
            <h3>Просмотренные фильмы</h3>
            {watched.length === 0 ? (
              <p>Нет просмотренных фильмов</p>
            ) : (
              <div className="carousel">
                {watched.map(movie => (
                  <article key={movie.id} className="card" onClick={() => setSelectedId(movie.id)}>
                    {movie.posterPath ? (
                      <img src={`https://image.tmdb.org/t/p/w300${movie.posterPath}`} alt={movie.title} className="poster" />
                    ) : (
                      <div className="no-poster">Нет постера</div>
                    )}
                    <div className="meta">
                      <h3 className="title">{movie.title}</h3>
                      <div className="sub">
                        <span>{movie.releaseDate}</span>
                      </div>
                    </div>
                  </article>
                ))}
              </div>
            )}
          </div>

          <div style={{marginTop:20}}>
            <h3>Избранные фильмы</h3>
            {favorites.length === 0 ? (
              <p>Нет избранных фильмов</p>
            ) : (
              <div className="carousel">
                {favorites.map(movie => (
                  <article key={movie.id} className="card" onClick={() => setSelectedId(movie.id)}>
                    {movie.posterPath ? (
                      <img src={`https://image.tmdb.org/t/p/w300${movie.posterPath}`} alt={movie.title} className="poster" />
                    ) : (
                      <div className="no-poster">Нет постера</div>
                    )}
                    <div className="meta">
                      <h3 className="title">{movie.title}</h3>
                      <div className="sub">
                        <span>{movie.releaseDate}</span>
                      </div>
                    </div>
                  </article>
                ))}
              </div>
            )}
          </div>
        </>
      )}

      {selectedId && (
        <MovieModal movieId={selectedId} onClose={() => setSelectedId(null)} />
      )}

      <div style={{marginTop:18}}>
        <button className="auth-button" onClick={onLogout}>Выйти из аккаунта</button>
      </div>
    </section>
  )
}
