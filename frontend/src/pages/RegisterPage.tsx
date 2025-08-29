import React, { useState } from "react"
import { apiFetch } from "../api/client"
import FormInput from "../components/FormInput"
import { useNavigate } from "react-router-dom"

// ********************************************************************************************
// * RegisterPage: User registration form (name, email, password)                            *
// *                                                                                         *
// * WHAT                                                                                    *
// * - Renders a form with three controlled inputs: name, email, password.                   *
// * - On submit: calls `apiFetch` POST /api/v1/auth/register.                               *
// * - If success: navigate to `/verify-email?token=...` with token from backend.            *
// * - If failure: displays an error message from API.                                       *
// *                                                                                         *
// * WHY                                                                                     *
// * - Centralizes registration logic in one place.                                          *
// * - Ensures consistent UX and error handling for onboarding flow.                         *
// ********************************************************************************************

export default function RegisterPage() {
    // Local state for inputs and error feedback
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const [name, setName] = useState("")
    const [error, setError] = useState<string | null>(null)
    const navigate = useNavigate()

    // Submit handler: call backend, then redirect or show error
    async function handleRegister(e: React.FormEvent) {
        e.preventDefault()
        try {
            const res = await apiFetch<{ email: string; roles: string[]; verificationToken: string }>(
                "/api/v1/auth/register",
                {
                    method: "POST",
                    body: JSON.stringify({ email, password, name }),
                }
            )

            // 👇 redirect direct with token - Must be updated for production
            navigate(`/verify-email?token=${res.verificationToken}`)
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
            <h1 className="text-xl font-bold mb-4">Register</h1>
            <form onSubmit={handleRegister}>
                <FormInput label="Name" value={name} onChange={e => setName(e.target.value)} />
                <FormInput label="Email" type="email" value={email} onChange={e => setEmail(e.target.value)} />
                <FormInput label="Password" type="password" value={password} onChange={e => setPassword(e.target.value)} />
                {error && <p className="text-red-500 mb-2">{error}</p>}
                <button type="submit" className="w-full bg-green-600 text-white py-2 rounded-lg hover:bg-green-700">
                    Register
                </button>
            </form>
        </div>
    )
}
