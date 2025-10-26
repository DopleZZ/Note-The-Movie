import React, { useEffect, useState } from 'react'
import { Routes, Route, useNavigate } from 'react-router-dom'
import PopularMovies from './pages/PopularMovies'
import Profile from './pages/Profile'
import AuthModal from './components/AuthModal'
import { getCurrentUser, logout } from './api/auth'

export default function App() {
  const [user, setUser] = useState(null)
  const [authOpen, setAuthOpen] = useState(false)
  const [authMode, setAuthMode] = useState('login')

  useEffect(() => {
    const u = getCurrentUser()
    if (u) setUser(u)
  }, [])

  const navigate = useNavigate()

  function openAuth(mode = 'login') {
    setAuthMode(mode)
    setAuthOpen(true)
  }

  function handleLogout() {
    logout()
    setUser(null)
    navigate('/')
  }

  function handleAuthSuccess() {
    const u = getCurrentUser()
    setUser(u)
  }

  return (
    <div className="app">
      <header className="header" style={{display:'flex',justifyContent:'space-between',alignItems:'center'}}>
        <div>
          <h1>Note The Movie</h1>
          <p>Составляй свои списки фильмов — пока показываем популярные</p>
        </div>

        <div className="header-actions">
          {user ? (
            <button className="auth-button" title={user} onClick={() => navigate('/profile')} aria-label="Profile">
              {/* simple user icon */}
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M12 12c2.761 0 5-2.239 5-5s-2.239-5-5-5-5 2.239-5 5 2.239 5 5 5zM4 20c0-2.761 4.477-5 8-5s8 2.239 8 5v1H4v-1z" fill="currentColor"/></svg>
            </button>
          ) : (
            <>
              <button className="auth-button" onClick={() => openAuth('login')} aria-label="Login">Войти</button>
              <button className="auth-button ghost" onClick={() => openAuth('register')} aria-label="Register">Регистрация</button>
            </>
          )}
        </div>
      </header>

      <main>
        <Routes>
          <Route path="/" element={<PopularMovies />} />
          <Route path="/profile" element={<Profile user={user} onLogout={handleLogout} />} />
        </Routes>
      </main>

      <footer className="footer">Data provided by TMDB</footer>

      {authOpen && (
        <AuthModal mode={authMode} onClose={() => setAuthOpen(false)} onSuccess={handleAuthSuccess} />
      )}
    </div>
  )
}
