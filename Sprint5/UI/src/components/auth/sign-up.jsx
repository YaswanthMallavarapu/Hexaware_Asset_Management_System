import React from 'react'
import { Link } from 'react-router-dom'
import { BsPersonBadge, BsPerson } from 'react-icons/bs'

const SignUp = () => {
  return (
    <div className="container-fluid admin-bg d-flex align-items-center justify-content-center vh-100">

      <div className="card border-0 shadow-sm p-4 text-center" style={{ minWidth: "320px" }}>

        <h5 className="fw-bold mb-2 admin-title">Sign Up</h5>
        <p className="text-muted small mb-4">Choose your role</p>

        <div className="d-flex justify-content-center gap-3 flex-wrap">

          <Link to="/manager/signup">
            <button className="btn add-btn d-flex align-items-center gap-2">
              <BsPersonBadge size={18} />
              Manager Signup
            </button>
          </Link>

          <Link to="/employee/signup">
            <button className="btn add-btn d-flex align-items-center gap-2">
              <BsPerson size={18} />
              Employee Signup
            </button>
          </Link>

        </div>

      </div>

    </div>
  )
}

export default SignUp