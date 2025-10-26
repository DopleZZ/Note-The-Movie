import React, { useState } from 'react'
import { login, register } from '../../api/auth'
import './AuthModal.css'

export default function AuthModal({ mode = 'login', onClose, onSuccess }) {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  async function handleSubmit(e) {
    e.preventDefault()
    setLoading(true)
    setError(null)
    try {
      if (mode === 'login') {
        await login(username, password)
      } else {
        await register(username, password)
      }
      onSuccess && onSuccess()
      onClose()
    } catch (ex) {
      setError(ex.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-overlay" onClick={(e) => e.target === e.currentTarget && onClose()}>
      <div className="auth-modal">
        <button className="auth-close" onClick={onClose} aria-label="Close">✕</button>
        <h3>{mode === 'login' ? 'Вход в аккаунт' : 'Регистрация'}</h3>
        <form onSubmit={handleSubmit} className="auth-form">
          <label>
            Пользователь
            <input value={username} onChange={(e) => setUsername(e.target.value)} required />
          </label>
          <label>
            Пароль
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
          </label>

          {error && <div className="auth-error">{error}</div>}

          <div className="auth-actions">
            <button type="submit" className="btn" disabled={loading}>{loading ? '...' : (mode === 'login' ? 'Войти' : 'Зарегистрироваться')}</button>
          </div>
        </form>
      </div>
    </div>
  )
}
