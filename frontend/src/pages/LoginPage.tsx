import React, { useState } from "react"
import { apiFetch } from "../api/client"
import FormInput from "../components/FormInput"
import { useNavigate } from "react-router-dom"

// ********************************************************************************************
// * LoginPage: Simple login form with email + password                                      *
// *                                                                                         *
// * WHAT                                                                                    *
// * - Renders a form with two controlled inputs (email + password).                         *
// * - On submit: calls `apiFetch` POST /api/v1/auth/login.                                  *
// * - If success: navigates to `/admin/profile`.                                            *
// * - If failure: shows an error message from API.                                             *
// *                                                                                         *
// * WHY                                                                                     *
// * - Centralized login flow, consistent error handling, simple UX.                         *
// ********************************************************************************************

export default function LoginPage() {
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const [error, setError] = useState<string | null>(null)
    const navigate = useNavigate()

    async function handleLogin(e: React.FormEvent) {
        e.preventDefault()
        try {
            await apiFetch("/api/v1/auth/login", {
                method: "POST",
                body: JSON.stringify({ email, password }),
            })
            navigate("/admin/profile")
        } catch (err: unknown) {
            if (err instanceof Error) {
                setError(err.message)
            } else {
                setError("Unexpected error")
            }
        }
    }


    return (
        <div className="max-w-md mx-auto mt-10 bg-white p-6 rounded-2xl shadow">
            <h1 className="text-xl font-bold mb-4">Login</h1>
            <form onSubmit={handleLogin}>
                <FormInput label="Email" type="email" value={email} onChange={e => setEmail(e.target.value)} />
                <FormInput label="Password" type="password" value={password} onChange={e => setPassword(e.target.value)} />
                {error && <p className="text-red-500 mb-2">{error}</p>}
                <button type="submit" className="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700">
                    Login
                </button>
            </form>
        </div>
    )
}