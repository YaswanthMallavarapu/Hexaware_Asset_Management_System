import axios from "axios";
import React, { useEffect, useState } from "react";
import {
  BsBoxSeam,
  BsArrowLeftRight,
  BsTools,
} from "react-icons/bs";
import { NavLink } from "react-router-dom";
import "../../styles/employee-dashboard.css";

const EmployeeDashboard = () => {
  const assetRequestApi =
    "http://localhost:8082/api/asset-request/count-by-user";
  const allocationApi =
    "http://localhost:8082/api/asset-allocation/count-by-user";
  const serviceRequestApi =
    "http://localhost:8082/api/service-request/count-by-user";

  const [assetRequestCount, setAssetRequestCount] = useState(0);
  const [assetAllocations, setAssetAllocations] = useState(0);
  const [serviceRequests, setServiceRequest] = useState(0);

  useEffect(() => {
    const config = {
      headers: {
        Authorization: "Bearer " + localStorage.getItem("token"),
      },
    };

    const fetchCount = async () => {
      try {
        const ar = await axios.get(assetRequestApi, config);
        const aa = await axios.get(allocationApi, config);
        const sr = await axios.get(serviceRequestApi, config);

        setAssetRequestCount(ar.data);
        setAssetAllocations(aa.data);
        setServiceRequest(sr.data);
      } catch (error) {
        console.log(error.message);
      }
    };

    fetchCount();
  }, []);

  const cards = [
    {
      icon: <BsBoxSeam size={26} color="#db2777" />,
      iconBg: "#fdf2f8",
      border: "#fbcfe8",
      value: assetRequestCount,
      label: "My Asset Requests",
      to: "/employee/asset-request",
    },
    {
      icon: <BsArrowLeftRight size={26} color="#ea580c" />,
      iconBg: "#fff7ed",
      border: "#fed7aa",
      value: assetAllocations,
      label: "My Allocations",
      to: "/employee/asset-allocation",
    },
    {
      icon: <BsTools size={26} color="#7c3aed" />,
      iconBg: "#f5f3ff",
      border: "#ddd6fe",
      value: serviceRequests,
      label: "My Service Requests",
      to: "/employee/service-request",
    },
  ];

  return (
    <div className="emp-dashboard-container">
      <div className="row g-4">
        {cards.map((card, index) => (
          <div className="col-6" key={index}>
            <NavLink to={card.to} className="emp-link">
              <div
                className="emp-card"
                style={{ borderColor: card.border }}
              >
                <div
                  className="emp-icon-box"
                  style={{
                    backgroundColor: card.iconBg,
                    borderColor: card.border,
                  }}
                >
                  {card.icon}
                </div>

                <div className="emp-count">{card.value}</div>
                <div className="emp-label">{card.label}</div>

                <div
                  className="emp-progress-bg"
                  style={{ backgroundColor: card.iconBg }}
                >
                  <div
                    className="emp-progress-fill"
                    style={{ backgroundColor: card.border }}
                  ></div>
                </div>
              </div>
            </NavLink>
          </div>
        ))}
      </div>
    </div>
  );
};

export default EmployeeDashboard;