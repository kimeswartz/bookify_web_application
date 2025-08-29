import { BrowserRouter, Routes, Route } from "react-router-dom"
import LoginPage from "../pages/LoginPage"
import RegisterPage from "../pages/RegisterPage"
import VerifyEmailPage from "../pages/VerifyEmailPage"
import ForgotPasswordPage from "../pages/ForgotPasswordPage"
import ResetPasswordPage from "../pages/ResetPasswordPage"
import ProfilePage from "../pages/ProfilePage"

// ********************************************************************************************
// * AppRouter: Central routing configuration for the app                                    *
// *                                                                                         *
// * WHAT                                                                                    *
// * - Uses React Router's <BrowserRouter>, <Routes>, <Route>.                               *
// * - Maps URL paths to page components (login, register, verify email, etc.).              *
// * - Keeps all navigation logic in one place.                                              *
// *                                                                                         *
// * WHY                                                                                     *
// * - A dedicated router file makes routes explicit and easy to manage.                     *
// * - Avoids scattering <Routes> across components (clean separation of concerns).          *
// * - Scales well: new pages just add a <Route> here.                                       *
// * - Supports browser history navigation (back/forward).                                   *
// ********************************************************************************************

export default function AppRouter() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/verify-email" element={<VerifyEmailPage />} />
                <Route path="/forgot-password" element={<ForgotPasswordPage />} />
                <Route path="/reset-password" element={<ResetPasswordPage />} />
                <Route path="/admin/profile" element={<ProfilePage />} />
            </Routes>
        </BrowserRouter>
    )
}