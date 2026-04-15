import axios from 'axios'
import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'

const Login = () => {


    const [username, setUsername] = useState(undefined)
    const [password, setPassword] = useState(undefined)
    const [token, setToken] = useState(undefined)
    const [errMsg,setErrMsg]=useState(undefined)
    const navigate =useNavigate()
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
        setErrMsg("Invalid Credentials")
    }
}




  return (
        <div className="container">
            <div className="row mt-4">
                <div className="col-md-3"></div>
                <div className="col-md-6">
                    <div className="row">
                        <div className="col-lg-12 card">
                            <div className="card-header">
                                Login Form
                            </div>

                            <div className="card-body">
                                <form action="" onSubmit={(e) => processLogin(e)}>
                                    {
                                        !(errMsg==undefined)&&
                                        (
                                            <div className="alert alert-danger">
                                                {errMsg}
                                            </div>
                                        )
                                    }
                                    <div className="mt-3">
                                        <label htmlFor="">Username : </label>
                                        <input type="text" className="form-control" required="required"
                                            onChange={(e) => setUsername(e.target.value)} />
                                    </div>
                                    <div className="mt-3">
                                        <label htmlFor="">Password : </label>
                                        <input type="password" className="form-control" required="required"
                                            onChange={(e) => setPassword(e.target.value)} />
                                    </div>
                                    <div className="mt-3">
                                        <input type="submit" value="Login" />
                                    </div>
                                    <div className="mt-3">
                                        <p>Don't have an account? <Link to="/sign-up">Sign Up</Link></p>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-md-3"></div>
            </div>

        </div>
    )
}


export default Login
