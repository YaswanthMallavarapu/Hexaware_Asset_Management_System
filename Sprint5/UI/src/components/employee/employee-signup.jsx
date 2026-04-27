import React, { useState } from 'react';
import axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';

const EmployeeSignup = () => {

  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [gender, setGender] = useState('MALE');
  const [contactNumber, setContactNumber] = useState('');
  const [designation, setDesignation] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const navigate = useNavigate();

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [formErrors, setFormErrors] = useState({});


  const validate = () => {
    const errors = {};

    if (!firstName.trim()) errors.firstName = "First name is required";
    if (!lastName.trim()) errors.lastName = "Last name is required";

    if (!contactNumber) {
      errors.contactNumber = "Contact number is required";
    } else if (!/^[0-9]{10}$/.test(contactNumber)) {
      errors.contactNumber = "Must be 10 digits";
    }

    if (!designation.trim()) errors.designation = "Designation required";

    if (!username.trim()) {
      errors.username = "Username required";
    } else if (username.length < 4) {
      errors.username = "Minimum 4 characters required";
    }

    if (!password) {
      errors.password = "Password required";
    } else if (password.length < 6) {
      errors.password = "Minimum 6 characters required";
    }

    return errors;
  };


  const handleSubmit = async (e) => {
    e.preventDefault();

    const errors = validate();
    if (Object.keys(errors).length > 0) {
      setFormErrors(errors);
      return;
    }

    setFormErrors({});
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      await axios.post("http://localhost:8082/api/employee/add", {
        firstName,
        lastName,
        gender,
        contactNumber,
        designation,
        username,
        password
      });

      setSuccess("Employee registered successfully");

      // reset form
      setFirstName('');
      setLastName('');
      setGender('MALE');
      setContactNumber('');
      setDesignation('');
      setUsername('');
      setPassword('');

      navigate('/login');

    } catch (err) {
      setError("Failed to register employee");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container-fluid py-5" style={{ backgroundColor: "#f0f4f8", minHeight: "100vh" }}>

      <div className="d-flex justify-content-center">
        <div
          style={{
            backgroundColor: "#fff",
            borderRadius: "20px",
            padding: "32px",
            width: "100%",
            maxWidth: "500px",
            boxShadow: "0 20px 60px rgba(0,0,0,0.15)",
          }}
        >

          <h5 className="fw-bold mb-3" style={{ color: "#1e293b" }}>
            Employee Signup
          </h5>

          {error && (
            <div className="alert alert-danger py-2 px-3 mb-3">
              {error}
            </div>
          )}

          {success && (
            <div className="alert alert-success py-2 px-3 mb-3">
              {success}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="row g-3">

              {/* First Name */}
              <div className="col-6">
                <label className="form-label fw-semibold">First Name</label>
                <input
                  type="text"
                  className="form-control"
                  value={firstName}
                  onChange={e => setFirstName(e.target.value)}
                />
                {formErrors.firstName && <small className="text-danger">{formErrors.firstName}</small>}
              </div>

              {/* Last Name */}
              <div className="col-6">
                <label className="form-label fw-semibold">Last Name</label>
                <input
                  type="text"
                  className="form-control"
                  value={lastName}
                  onChange={e => setLastName(e.target.value)}
                />
                {formErrors.lastName && <small className="text-danger">{formErrors.lastName}</small>}
              </div>

              {/* Gender */}
              <div className="col-6">
                <label className="form-label fw-semibold">Gender</label>
                <select
                  className="form-control"
                  value={gender}
                  onChange={e => setGender(e.target.value)}
                >
                  <option value="MALE">Male</option>
                  <option value="FEMALE">Female</option>
                </select>
              </div>

              {/* Contact */}
              <div className="col-6">
                <label className="form-label fw-semibold">Contact Number</label>
                <input
                  type="text"
                  className="form-control"
                  value={contactNumber}
                  onChange={e => setContactNumber(e.target.value.replace(/\D/g, ''))}
                />
                {formErrors.contactNumber && <small className="text-danger">{formErrors.contactNumber}</small>}
              </div>

              {/* Designation */}
              <div className="col-12">
                <label className="form-label fw-semibold">Designation</label>
                <input
                  type="text"
                  className="form-control"
                  value={designation}
                  onChange={e => setDesignation(e.target.value)}
                />
                {formErrors.designation && <small className="text-danger">{formErrors.designation}</small>}
              </div>

              {/* Username */}
              <div className="col-12">
                <label className="form-label fw-semibold">Username</label>
                <input
                  type="text"
                  className="form-control"
                  value={username}
                  onChange={e => setUsername(e.target.value)}
                />
                {formErrors.username && <small className="text-danger">{formErrors.username}</small>}
              </div>

              {/* Password */}
              <div className="col-12">
                <label className="form-label fw-semibold">Password</label>
                <input
                  type="password"
                  className="form-control"
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                />
                {formErrors.password && <small className="text-danger">{formErrors.password}</small>}
              </div>

            </div>

            <button
              type="submit"
              className="btn w-100 mt-4"
              disabled={loading}
              style={{
                borderRadius: "10px",
                backgroundColor: "#2563eb",
                color: "#fff",
                border: "none"
              }}
            >
              {loading ? "Submitting..." : "Sign Up"}
            </button>

            <div className="text-center mt-3">
              <p className="small text-muted mb-0">
                Already have an account? <Link to="/login">Login</Link>
              </p>
            </div>

          </form>

        </div>
      </div>

    </div>
  );
};

export default EmployeeSignup;