import { useEffect, useState } from "react"
import { useSearchParams } from "react-router-dom"
import { apiFetch } from "../api/client"

// ********************************************************************************************
// * VerifyEmailPage: Handles email verification via token                                   *
// *                                                                                         *
// * WHAT                                                                                    *
// * - Reads `token` from query string (?token=...).                                         *
// * - Calls backend `/api/v1/auth/verify-email?token=...`.                                  *
// * - On success: shows "Email verified!" message.                                          *
// * - On failure: shows "Verification failed." message.                                     *
// *                                                                                         *
// * WHY                                                                                     *
// * - Provides user feedback immediately after clicking the email link.                     *
// * - Connects registration flow with backend verification logic.                           *
// ********************************************************************************************

export default function VerifyEmailPage() {
    const [searchParams] = useSearchParams()
    const token = searchParams.get("token")
    const [status, setStatus] = useState("Verifying...") // UI feedback state

    useEffect(() => {
        if (token) {
            // Call backend to verify token
            apiFetch(`/api/v1/auth/verify-email?token=${token}`)
                .then(() => setStatus("Email verified! You can now login."))
                .catch(() => setStatus("Verification failed."))
        }
    }, [token])

    return (
        <div className="max-w-md mx-auto mt-10 bg-white p-6 rounded-2xl shadow text-center">
            <h1 className="text-xl font-bold mb-4">Verify Email</h1>
            <p>{status}</p>
        </div>
    )
}