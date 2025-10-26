import React from 'react'

export default function Profile({ user, onLogout }) {
  return (
    <section style={{padding:20}}>
      <h2>Профиль пользователя</h2>
      <div style={{marginTop:12}}>
        <strong>Имя пользователя:</strong>
        <div style={{marginTop:6}}>{user || '—'}</div>
      </div>

      <div style={{marginTop:18}}>
        <button className="auth-button" onClick={onLogout}>Выйти из аккаунта</button>
      </div>
    </section>
  )
}
