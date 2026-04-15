import React from "react";
import { NavLink, useNavigate } from "react-router-dom";
import {
  FaBars,
  FaTachometerAlt,
  FaUsers,
  FaTags,
  FaLaptop,
  FaSignOutAlt,
  FaClipboardList,
  FaExchangeAlt,
  FaWrench,
  FaSearchDollar,
} from "react-icons/fa";

const ManagerSidebar = ({ isOpen, setIsOpen }) => {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login");
  };

  const navItems = [
    {
      to: "/manager/dashboard",
      icon: <FaTachometerAlt />,
      label: "Dashboard",
    },
    {
      to: "/manager/employees",
      icon: <FaUsers />,
      label: "Employees",
    },
    // {
    //   to: "/manager/categories",
    //   icon: <FaTags />,
    //   label: "Asset Categories",
    // },
    // {
    //   to: "/manager/assets",
    //   icon: <FaLaptop />,
    //   label: "Assets",
    // },
    {
      to: "/manager/asset-request",
      icon: <FaClipboardList />,
      label: "Asset Request",
    },
    {
      to: "/manager/asset-allocations",
      icon: <FaExchangeAlt />,
      label: "Asset Allocations",
    },
    {
      to: "/manager/service-request",
      icon: <FaWrench />,
      label: "Service Requests",
    },
    {
      to: "/manager/audit",
      icon: <FaSearchDollar />,
      label: "Asset Audits",
    },
  ];

  return (
    <div
      className="border-end bg-white shadow-sm d-flex flex-column"
      style={{
        width: isOpen ? "240px" : "80px",
        transition: "width 0.3s ease",
      }}
    >
      {/* Toggle Button */}
      <div className="d-flex align-items-center justify-content-between p-3">
        {isOpen && (
          <h6 className="fw-bold mb-0 text-dark">Asset Management</h6>
        )}
        <FaBars
          role="button"
          className="text-dark"
          onClick={() => setIsOpen(!isOpen)}
        />
      </div>

      {/* Nav Items */}
      <ul className="nav nav-pills flex-column px-2">
        {navItems.map((item) => (
          <li className="nav-item mb-2" key={item.to}>
            <NavLink
              to={item.to}
              className="nav-link text-dark d-flex align-items-center"
            >
              {item.icon}
              {isOpen && <span className="ms-3">{item.label}</span>}
            </NavLink>
          </li>
        ))}
      </ul>

      {/* Logout */}
      <div className="mt-auto p-3">
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

export default ManagerSidebar;