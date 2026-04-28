import React, { useEffect, useState } from "react";
import axios from "axios";
import { Chart } from "primereact/chart";
import {
  BsPeople,
  BsTools,
  BsBoxSeam,
  BsClipboardCheck,
  BsArrowLeftRight,
} from "react-icons/bs";
import { NavLink } from "react-router-dom";

const ManagerDashboard = () => {
  const [counts, setCounts] = useState({
    asset: 0,
    category: 0,
    employee: 0,
    serviceReq: 0,
    assetReq: 0,
    audit: 0,
    allocation: 0,
  });

  const [chartData, setChartData] = useState({});
  const [chartOptions, setChartOptions] = useState({});

  const BASE = "http://localhost:8082/api";

  useEffect(() => {
    const fetchCounts = async () => {
      try {
        const config = {
          headers: {
            Authorization: "Bearer " + localStorage.getItem("token"),
          },
        };

        const [
          assetRes,
          categoryRes,
          employeeRes,
          serviceReqRes,
          assetReqRes,
          auditRes,
          allocationRes,
        ] = await Promise.all([
          axios.get(`${BASE}/asset/count`, config),
          axios.get(`${BASE}/asset-category/count`, config),
          axios.get(`${BASE}/employee/count`, config),
          axios.get(`${BASE}/service-request/count`, config),
          axios.get(`${BASE}/asset-request/count`, config),
          axios.get(`${BASE}/asset-audit/count`, config),
          axios.get(`${BASE}/asset-allocation/count`, config),
        ]);

        const newCounts = {
          asset: assetRes.data,
          category: categoryRes.data,
          employee: employeeRes.data,
          serviceReq: serviceReqRes.data,
          assetReq: assetReqRes.data,
          audit: auditRes.data,
          allocation: allocationRes.data,
        };

        setCounts(newCounts);

        const documentStyle = getComputedStyle(document.documentElement);

        const data = {
          labels: [
            "Assets",
            "Categories",
            "Employees",
            "Service Requests",
            "Asset Requests",
            "Audits",
            "Allocations",
          ],
          datasets: [
            {
              data: Object.values(newCounts),
              backgroundColor: [
                "#3b82f6",
                "#eab308",
                "#22c55e",
                "#ec4899",
                "#f97316",
                "#8b5cf6",
                "#06b6d4",
              ],
              hoverBackgroundColor: [
                "#60a5fa",
                "#facc15",
                "#4ade80",
                "#f472b6",
                "#fb923c",
                "#a78bfa",
                "#22d3ee",
              ],
            },
          ],
        };

        const options = {
          plugins: {
            legend: {
              position: "bottom",
              labels: {
                usePointStyle: true,
              },
            },
          },
        };

        setChartData(data);
        setChartOptions(options);
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
      value: counts.employee,
      label: "Total Employees",
      to: "/manager/employees",
    },
    {
      icon: <BsBoxSeam size={26} color="#db2777" />,
      iconBg: "#fdf2f8",
      border: "#fbcfe8",
      value: counts.assetReq,
      label: "Asset Requests",
      to: "/manager/asset-request",
    },
    {
      icon: <BsArrowLeftRight size={26} color="#ea580c" />,
      iconBg: "#fff7ed",
      border: "#fed7aa",
      value: counts.allocation,
      label: "Asset Allocations",
      to: "/manager/asset-allocations",
    },
    {
      icon: <BsTools size={26} color="#7c3aed" />,
      iconBg: "#f5f3ff",
      border: "#ddd6fe",
      value: counts.serviceReq,
      label: "Service Requests",
      to: "/manager/service-request",
    },
    {
      icon: <BsClipboardCheck size={26} color="#0891b2" />,
      iconBg: "#ecfeff",
      border: "#a5f3fc",
      value: counts.audit,
      label: "Total Audits",
      to: "/manager/audit",
    },
  ];

  return (
    <div
      className="container-fluid py-4 px-4"
      style={{ backgroundColor: "#f0f4f8", minHeight: "100vh" }}
    >
      <div className="row mb-4">
        <div className="col-12">
          <div
            className="card border-0 p-4"
            style={{
              borderRadius: "16px",
              boxShadow: "0 4px 16px rgba(0,0,0,0.08)",
            }}
          >
            <h5 style={{ fontWeight: "600", marginBottom: "20px" }}>
              Dashboard Overview
            </h5>
            <div className="d-flex justify-content-center">
              <Chart
                type="pie"
                data={chartData}
                options={chartOptions}
                style={{ width: "350px" }}
              />
            </div>
          </div>
        </div>
      </div>

      <div className="row g-4">
        {cards.map((card, index) => (
          <div className="col-md-4 col-sm-6" key={index}>
            <NavLink to={card.to} style={{ textDecoration: "none" }}>
              <div
                className="card h-100 border-0"
                style={{
                  borderRadius: "16px",
                  boxShadow: "0 2px 12px rgba(0,0,0,0.07)",
                  transition: "all 0.2s ease",
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.transform = "translateY(-5px)";
                  e.currentTarget.style.boxShadow =
                    "0 10px 25px rgba(0,0,0,0.12)";
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.transform = "translateY(0)";
                  e.currentTarget.style.boxShadow =
                    "0 2px 12px rgba(0,0,0,0.07)";
                }}
              >
                <div className="card-body p-4">
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

                  <div
                    style={{
                      fontSize: "2rem",
                      fontWeight: "700",
                      color: "#1e293b",
                    }}
                  >
                    {card.value}
                  </div>

                  <div
                    style={{
                      fontSize: "0.9rem",
                      color: "#64748b",
                      marginTop: "6px",
                    }}
                  >
                    {card.label}
                  </div>

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