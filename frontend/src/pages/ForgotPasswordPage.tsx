import React, { useState } from "react"
import { apiFetch } from "../api/client"
import FormInput from "../components/FormInput"

// *******************************************************************************************
// * ForgotPasswordPage: Request password reset link                                         *
// *                                                                                         *
// * WHAT                                                                                    *
// * - Renders a form with one input (email).                                                *
// * - On submit: POST /api/v1/auth/forgot-password with email.                              *
// * - Always sets a status message: "If an account exists..." (no account leak).            *
// *                                                                                         *
// * WHY                                                                                     *
// * - Lets users initiate a secure password reset flow. *
// * a - Response message avoid revealing if email exists (security best practice).          *
// *******************************************************************************************

export default function ForgotPasswordPage() {
    // Local state for an email and feedback message
    const [email, setEmail] = useState("")
    const [status, setStatus] = useState<string | null>(null)

    // Handle form submit: request password reset
    async function handleForgot(e: React.FormEvent) {
        e.preventDefault()
        await apiFetch("/api/v1/auth/forgot-password", {
            method: "POST",
            body: JSON.stringify({ email }),
        })
        // Generic response (prevents account enumeration)
        setStatus("If an account exists, a reset link has been sent.")
    }

    return (
        <div className="max-w-md mx-auto mt-10 bg-white p-6 rounded-2xl shadow">
            <h1 className="text-xl font-bold mb-4">Forgot Password</h1>
            <form onSubmit={handleForgot}>
                <FormInput label="Email" type="email" value={email} onChange={e => setEmail(e.target.value)} />
                <button type="submit" className="w-full bg-purple-600 text-white py-2 rounded-lg hover:bg-purple-700">
                    Send reset link
                </button>
            </form>
            {status && <p className="mt-2 text-green-600">{status}</p>}
        </div>
    )
}