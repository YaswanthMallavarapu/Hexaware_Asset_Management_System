import React from "react";
import { NavLink, useNavigate } from "react-router-dom";
import {
  FaBars,
  FaTachometerAlt,
  FaUserShield,
  FaUserTie,
  FaTags,
  FaBoxOpen,
  FaSignOutAlt,
  FaUsers
} from "react-icons/fa";

const Sidebar = ({ isOpen, setIsOpen }) => {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login");
  };

  return (
    <div
      className="border-end bg-white shadow-sm d-flex flex-column"
      style={{
        width: isOpen ? "240px" : "80px",
        transition: "width 0.3s ease"
      }}
    >
      {/* Toggle Button */}
      <div className="d-flex align-items-center justify-content-between p-3">
        {isOpen && (
          <h6 className="fw-bold mb-0 text-dark">
            Asset Management
          </h6>
        )}

        <FaBars
          role="button"
          className="text-dark"
          onClick={() => setIsOpen(!isOpen)}
        />
      </div>

      <ul className="nav nav-pills flex-column px-2">

        <li className="nav-item mb-2">
          <NavLink to="/employee/dashboard" className="nav-link text-dark d-flex align-items-center">
            <FaTachometerAlt />
            {isOpen && <span className="ms-3">Dashboard</span>}
          </NavLink>
        </li>

        <li className="nav-item mb-2">
          <NavLink to="/employee/assets" className="nav-link text-dark d-flex align-items-center">
            <FaUserShield />
            {isOpen && <span className="ms-3">Assets</span>}
          </NavLink>
        </li>



        <li className="nav-item mb-2">
          <NavLink to="/employee/asset-request" className="nav-link text-dark d-flex align-items-center">
            <FaTags />
            {isOpen && <span className="ms-3">Asset Request</span>}
          </NavLink>
        </li>

        <li className="nav-item mb-2">
          <NavLink to="/employee/asset-allocation" className="nav-link text-dark d-flex align-items-center">
            <FaBoxOpen />
            {isOpen && <span className="ms-3">Assets Allocated</span>}
          </NavLink>
        </li>

        <li className="nav-item mb-2">
          <NavLink to="/employee/service-request" className="nav-link text-dark d-flex align-items-center">
            <FaTags />
            {isOpen && <span className="ms-3">Service Request</span>}
          </NavLink>
        </li>







      </ul>

      {/* Logout */}
      <div className="mt-auto p-3">

        <ul className="nav nav-pills flex-column px-2">

          <li className="nav-item mb-2" >
            <NavLink
              to="/employee/profile"
              className="nav-link text-dark d-flex align-items-center"
            >
              <FaUsers />
              {isOpen && <span className="ms-3">Profile</span>}
            </NavLink>
          </li>

        </ul>
        <button
          onClick={handleLogout}
          className="btn btn-outline-danger w-100 d-flex align-items-center justify-content-center"
        >
          <FaSignOutAlt />
          {isOpen && <span className="ms-2">Logout</span>}
        </button>
      </div>
    </div>
  );
};

export default Sidebar;