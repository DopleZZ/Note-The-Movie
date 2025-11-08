import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import MovieModal from '../components/MovieModal'

// Minimal public profile view that fetches lists for a given user id.
// Assumes new backend endpoints: /api/users/{id}/watched and /api/users/{id}/favorites
// If those endpoints don't exist yet, this component will show an error.
export default function PublicProfile() {
  const { id } = useParams()
  const [watched, setWatched] = useState([])
  const [favorites, setFavorites] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [selectedId, setSelectedId] = useState(null)
  const [userMeta, setUserMeta] = useState(null)

  useEffect(() => {
    async function load() {
      try {
        const base = import.meta.env.VITE_API_BASE || ''
        const [metaRes, wRes, fRes] = await Promise.all([
          fetch(`${base}/api/users/${id}`),
          fetch(`${base}/api/users/${id}/watched`),
          fetch(`${base}/api/users/${id}/favorites`)
        ])
        if (!metaRes.ok || !wRes.ok || !fRes.ok) throw new Error('Не удалось загрузить публичный профиль')
        const [meta, w, f] = await Promise.all([metaRes.json(), wRes.json(), fRes.json()])
        setUserMeta(meta)
        setWatched(w)
        setFavorites(f)
      } catch (err) {
        setError(err.message)
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [id])

  return (
    <section style={{padding:20}}>
  <h2>Публичный профиль пользователя {userMeta ? userMeta.username : id}</h2>
      {loading ? (
        <div>Загрузка...</div>
      ) : error ? (
        <div style={{color:'red'}}>Ошибка: {error}</div>
      ) : (
        <>
          <div style={{marginTop:20}}>
            <h3>Просмотренные фильмы</h3>
            {watched.length === 0 ? <p>Нет просмотренных фильмов</p> : (
              <div className="carousel">
                {watched.map(movie => (
                  <article key={movie.id} className="card" onClick={() => setSelectedId(movie.id)}>
                    {(movie.posterPath || movie.posterUrl) && (movie.posterPath || movie.posterUrl).startsWith('http') ? (
                      <img src={(movie.posterPath || movie.posterUrl)} alt={movie.title || movie.nameRu || movie.nameOriginal} className="poster" />
                    ) : <div className="no-poster">Нет постера</div>}
                    <div className="meta">
                      <h3 className="title">{movie.title || movie.nameRu || movie.nameOriginal}</h3>
                      <div className="sub"><span>{movie.releaseDate || movie.year || ''}</span></div>
                    </div>
                  </article>
                ))}
              </div>
            )}
          </div>
          <div style={{marginTop:20}}>
            <h3>Избранные фильмы</h3>
            {favorites.length === 0 ? <p>Нет избранных фильмов</p> : (
              <div className="carousel">
                {favorites.map(movie => (
                  <article key={movie.id} className="card" onClick={() => setSelectedId(movie.id)}>
                    {(movie.posterPath || movie.posterUrl) && (movie.posterPath || movie.posterUrl).startsWith('http') ? (
                      <img src={(movie.posterPath || movie.posterUrl)} alt={movie.title || movie.nameRu || movie.nameOriginal} className="poster" />
                    ) : <div className="no-poster">Нет постера</div>}
                    <div className="meta">
                      <h3 className="title">{movie.title || movie.nameRu || movie.nameOriginal}</h3>
                      <div className="sub"><span>{movie.releaseDate || movie.year || ''}</span></div>
                    </div>
                  </article>
                ))}
              </div>
            )}
          </div>
        </>
      )}
      {selectedId && <MovieModal movieId={selectedId} onClose={() => setSelectedId(null)} />}
    </section>
  )
}
