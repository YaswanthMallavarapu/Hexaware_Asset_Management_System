import axios from 'axios'
import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'

const Login = () => {


    const [username, setUsername] = useState(undefined)
    const [password, setPassword] = useState(undefined)
    const [token, setToken] = useState(undefined)
    const [errMsg, setErrMsg] = useState(undefined)
    const navigate = useNavigate()
    const loginApi = "http://localhost:8082/api/auth/login"
    const detailsApi = "http://localhost:8082/api/auth/user-details"


    const processLogin = async (e) => {
        e.preventDefault()

        try {

            const encodedString = window.btoa(username + ":" + password)

            const config = {
                headers: {
                    Authorization: "Basic " + encodedString
                }
            }

            const response = await axios.get(loginApi, config)

            setToken(response.data.token)
            localStorage.setItem("token", response.data.token)

            const detailsConfig = {
                headers: {
                    Authorization: "Bearer " + response.data.token
                }
            }

            const detailsResponse = await axios.get(detailsApi, detailsConfig)

            switch (detailsResponse.data.role) {
                case "ADMIN":
                    navigate("/admin/dashboard")
                    break
                case "MANAGER":
                    navigate("/manager/dashboard")
                    break
                case "EMPLOYEE":
                    navigate("/employee/dashboard")
                    break

            }

        } catch (error) {
            if (error?.response?.status === 400)
                setErrMsg("Account not Verified yet.")
            else
                setErrMsg("Invalid Credentials")
        }
    }




    return (
        <div className="container-fluid py-5 admin-bg">

            <div className="row justify-content-center">
                <div className="col-12 col-sm-10 col-md-6 col-lg-4">

                    <div className="card border-0 shadow-sm">

                        {/* Header */}
                        <div className="card-body p-4">

                            <div className="text-center mb-4">
                                <h5 className="fw-bold mb-1 admin-title">Login</h5>
                                <p className="text-muted small mb-0">
                                    Access your account
                                </p>
                            </div>

                            {/* Error */}
                            {
                                errMsg &&
                                <div className="alert alert-danger py-2 text-center small">
                                    {errMsg}
                                </div>
                            }

                            {/* Form */}
                            <form onSubmit={(e) => processLogin(e)}>

                                <div className="mb-3">
                                    <label className="form-label small">Username</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        required
                                        onChange={(e) => setUsername(e.target.value)}
                                    />
                                </div>

                                <div className="mb-3">
                                    <label className="form-label small">Password</label>
                                    <input
                                        type="password"
                                        className="form-control"
                                        required
                                        onChange={(e) => setPassword(e.target.value)}
                                    />
                                </div>

                                <button type="submit" className="btn add-btn w-100 mt-2">
                                    Login
                                </button>

                                <div className="text-center mt-3">
                                    <p className="small text-muted mb-0">
                                        Don't have an account?{" "}
                                        <Link to="/sign-up">Sign Up</Link>
                                    </p>
                                </div>

                            </form>

                        </div>
                    </div>

                </div>
            </div>

        </div>
    )
}


export default Login
