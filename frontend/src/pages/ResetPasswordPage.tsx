import React, { useState } from "react"
import { useSearchParams } from "react-router-dom"
import { apiFetch } from "../api/client"
import FormInput from "../components/FormInput"

// *******************************************************************************************
// * ResetPasswordPage: Final step of password reset flow                                    *
// *                                                                                         *
// * WHAT                                                                                    *
// * - Reads reset `token` from query string (?token=...).                                   *
// * - Lets user set a new password via a form.                                              *
// * - On submit: POST /api/v1/auth/reset-password with { token, newPassword }.              *
// * - Shows a success message when the password is updated.                                 *
// *                                                                                         *
// * WHY                                                                                     *
// * - Completes secure password reset flow after an email link.                             *
// * - Keeps UX simple: one field + clear confirmation.                                      *
// *******************************************************************************************

export default function ResetPasswordPage() {
    const [searchParams] = useSearchParams()
    const token = searchParams.get("token")
    const [password, setPassword] = useState("")
    const [status, setStatus] = useState<string | null>(null)

    // Handle form submit: send token + new password to backend
    async function handleReset(e: React.FormEvent) {
        e.preventDefault()
        await apiFetch("/api/v1/auth/reset-password", {
            method: "POST",
            body: JSON.stringify({ token, newPassword: password }),
        })
        setStatus("Password reset successful. You can now login.")
    }

    return (
        <div className="max-w-md mx-auto mt-10 bg-white p-6 rounded-2xl shadow">
            <h1 className="text-xl font-bold mb-4">Reset Password</h1>
            <form onSubmit={handleReset}>
                <FormInput label="New Password" type="password" value={password} onChange={e => setPassword(e.target.value)} />
                <button type="submit" className="w-full bg-orange-600 text-white py-2 rounded-lg hover:bg-orange-700">
                    Reset Password
                </button>
            </form>
            {status && <p className="mt-2 text-green-600">{status}</p>}
        </div>
    )
}