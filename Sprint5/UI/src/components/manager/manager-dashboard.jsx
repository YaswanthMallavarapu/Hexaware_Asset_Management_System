import React, { useEffect, useState } from "react";
import axios from "axios";
import {
  BsLaptop,
  BsGrid,
  BsPeople,
  BsTools,
  BsBoxSeam,
  BsClipboardCheck,
  BsArrowLeftRight,
} from "react-icons/bs";
import { NavLink } from "react-router-dom";

const ManagerDashboard = () => {
  const [assetCount, setAssetCount] = useState(0);
  const [categoryCount, setCategoryCount] = useState(0);
  const [employeeCount, setEmployeeCount] = useState(0);
  const [serviceRequestCount, setServiceRequestCount] = useState(0);
  const [assetRequestCount, setAssetRequestCount] = useState(0);
  const [auditCount, setAuditCount] = useState(0);
  const [allocationCount, setAllocationCount] = useState(0);

  const BASE = "http://localhost:8082/api";
  const assetApi = `${BASE}/asset/count`;
  const categoryApi = `${BASE}/asset-category/count`;
  const employeeApi = `${BASE}/employee/count`;
  const serviceReqApi = `${BASE}/service-request/count`;
  const assetReqApi = `${BASE}/asset-request/count`;
  const auditApi = `${BASE}/asset-audit/count`;
  const allocationApi = `${BASE}/asset-allocation/count`;

  useEffect(() => {
    const fetchCounts = async () => {
      try {
        const config = {
          headers: {
            Authorization: "Bearer " + localStorage.getItem("token"),
          },
        };


        const employeeRes = await axios.get(employeeApi, config);
        const categoryRes = await axios.get(categoryApi, config);
        const assetRes = await axios.get(assetApi, config);
        const assetReqRes = await axios.get(assetReqApi, config);
        const allocationRes = await axios.get(allocationApi, config);
        const serviceReqRes = await axios.get(serviceReqApi, config);
        const auditRes = await axios.get(auditApi, config);

        setAssetCount(assetRes.data);
        setCategoryCount(categoryRes.data);
        setEmployeeCount(employeeRes.data);
        setServiceRequestCount(serviceReqRes.data);
        setAssetRequestCount(assetReqRes.data);
        setAuditCount(auditRes.data);
        setAllocationCount(allocationRes.data);
      } catch (error) {
        console.error(error);
      }
    };
    fetchCounts();
  }, []);

  const cards = [
    {
      icon: <BsPeople size={26} color="#d97706" />,
      iconBg: "#fffbeb",
      border: "#fde68a",
      value: employeeCount,
      label: "Total Employees",
      to:"/manager/employees"
    },
    // {
    //   icon: <BsGrid size={26} color="#16a34a" />,
    //   iconBg: "#f0fdf4",
    //   border: "#bbf7d0",
    //   value: categoryCount,
    //   label: "Asset Categories",
    // },
    // {
    //   icon: <BsLaptop size={26} color="#2563eb" />,
    //   iconBg: "#eff6ff",
    //   border: "#bfdbfe",
    //   value: assetCount,
    //   label: "Total Assets",
    // },
    {
      icon: <BsBoxSeam size={26} color="#db2777" />,
      iconBg: "#fdf2f8",
      border: "#fbcfe8",
      value: assetRequestCount,
      label: "Asset Requests",
      to:"/manager/asset-request"
    },
    {
      icon: <BsArrowLeftRight size={26} color="#ea580c" />,
      iconBg: "#fff7ed",
      border: "#fed7aa",
      value: allocationCount,
      label: "Asset Allocations",
      to:"/manager/asset-allocations"
    },
    {
      icon: <BsTools size={26} color="#7c3aed" />,
      iconBg: "#f5f3ff",
      border: "#ddd6fe",
      value: serviceRequestCount,
      label: "Service Requests",
      to:"/manager/service-request"
    },
    {
      icon: <BsClipboardCheck size={26} color="#0891b2" />,
      iconBg: "#ecfeff",
      border: "#a5f3fc",
      value: auditCount,
      label: "Total Audits",
      to:"/manager/audit"
    },
  ];

  return (
    <div
      className="container-fluid py-4 px-4"
      style={{ backgroundColor: "#f0f4f8", minHeight: "100vh" }}
    >
      <div className="row g-4">
        {cards.map((card, index) => (
          
          <div
            className="col-6"
            key={index}
            // Make the last card full-width if total cards is odd
            style={
              cards.length % 2 !== 0 && index === cards.length - 1
                ? { flex: "0 0 100%", maxWidth: "100%" }
                : {}
            }
          >
            <NavLink to={card.to}>
            <div
              className="card h-100 border-0"
              style={{
                borderRadius: "16px",
                boxShadow: "0 2px 12px rgba(0,0,0,0.07)",
                transition: "transform 0.2s ease, box-shadow 0.2s ease",
                cursor: "default",
              }}
              onMouseEnter={(e) => {
                e.currentTarget.style.transform = "translateY(-4px)";
                e.currentTarget.style.boxShadow =
                  "0 8px 24px rgba(0,0,0,0.12)";
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.transform = "translateY(0)";
                e.currentTarget.style.boxShadow =
                  "0 2px 12px rgba(0,0,0,0.07)";
              }}
            >
              <div className="card-body p-4">
                {/* Icon Box */}
                <div
                  style={{
                    width: 52,
                    height: 52,
                    borderRadius: "14px",
                    backgroundColor: card.iconBg,
                    border: `1.5px solid ${card.border}`,
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    marginBottom: "16px",
                  }}
                >
                  {card.icon}
                </div>

                {/* Count */}
                <div
                  style={{
                    fontSize: "2rem",
                    fontWeight: "700",
                    color: "#1e293b",
                    lineHeight: 1.1,
                  }}
                >
                  {card.value}
                </div>

                {/* Label */}
                <div
                  style={{
                    fontSize: "0.875rem",
                    color: "#64748b",
                    marginTop: "6px",
                    fontWeight: 500,
                  }}
                >
                  {card.label}
                </div>

                {/* Progress Bar */}
                <div
                  style={{
                    marginTop: "16px",
                    height: "4px",
                    borderRadius: "999px",
                    backgroundColor: card.iconBg,
                  }}
                >
                  <div
                    style={{
                      height: "100%",
                      width: "60%",
                      borderRadius: "999px",
                      backgroundColor: card.border,
                    }}
                  />
                </div>
              </div>
            </div>
            </NavLink>
            
          </div>
          
        ))}
      </div>
    </div>
  );
};

export default ManagerDashboard;