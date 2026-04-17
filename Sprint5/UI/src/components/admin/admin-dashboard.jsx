import React, { useEffect, useState } from "react";
import axios from "axios";
import { BsPeople, BsLaptop, BsExclamationCircle, BsClipboardCheck } from "react-icons/bs";

const AdminDashboard = () => {

  const [adminCount, setAdminCount] = useState(0);
  const [managerCount, setManagerCount] = useState(0);
  const [categoriesCount, setCategoriesCount] = useState(0);
  const [assetCount, setAssetCount] = useState(0);

  const assetApi = "http://localhost:8082/api/asset/count";
  const categorieApi = "http://localhost:8082/api/asset-category/count";
  const adminApi = "http://localhost:8082/api/admin/count";
  const managerApi = "http://localhost:8082/api/manager/count";

  useEffect(() => {
    const fetchCounts = async () => {
      try {
        const config = {
          headers: {
            Authorization: "Bearer " + localStorage.getItem("token")
          }
        };
        const [assetRes, categoryRes, managerRes, adminRes] = await Promise.all([
          axios.get(assetApi, config),
          axios.get(categorieApi, config),
          axios.get(managerApi, config),
          axios.get(adminApi, config),
        ]);
        setAssetCount(assetRes.data);
        setCategoriesCount(categoryRes.data);
        setManagerCount(managerRes.data);
        setAdminCount(adminRes.data);
      } catch (error) {
        console.error(error);
      }
    };
    fetchCounts();
  }, []);

  const cards = [
    {
      icon: <BsPeople size={26} color="#2563eb" />,
      iconBg: "#eff6ff",
      border: "#bfdbfe",
      value: adminCount,
      label: "Total Admins",
    },
    {
      icon: <BsLaptop size={26} color="#16a34a" />,
      iconBg: "#f0fdf4",
      border: "#bbf7d0",
      value: assetCount,
      label: "Total Assets",
    },
    {
      icon: <BsExclamationCircle size={26} color="#d97706" />,
      iconBg: "#fffbeb",
      border: "#fde68a",
      value: categoriesCount,
      label: "Total Categories",
    },
    {
      icon: <BsClipboardCheck size={26} color="#7c3aed" />,
      iconBg: "#f5f3ff",
      border: "#ddd6fe",
      value: managerCount,
      label: "Total Managers",
    },
  ];

  return (
    <div className="container-fluid py-4 px-4" style={{ backgroundColor: "#f0f4f8", minHeight: "100vh" }}>
      <div className="row g-4">
        {cards.map((card, index) => (
          <div className="col-6" key={index}>
            <div
              className="card h-100 border-0"
              style={{
                borderRadius: "16px",
                boxShadow: "0 2px 12px rgba(0,0,0,0.07)",
                transition: "transform 0.2s ease, box-shadow 0.2s ease",
                cursor: "default",
              }}
              onMouseEnter={e => {
                e.currentTarget.style.transform = "translateY(-4px)";
                e.currentTarget.style.boxShadow = "0 8px 24px rgba(0,0,0,0.12)";
              }}
              onMouseLeave={e => {
                e.currentTarget.style.transform = "translateY(0)";
                e.currentTarget.style.boxShadow = "0 2px 12px rgba(0,0,0,0.07)";
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

                <div style={{ fontSize: "2rem", fontWeight: "700", color: "#1e293b", lineHeight: 1.1 }}>
                  {card.value}
                </div>
                <div style={{ fontSize: "0.875rem", color: "#64748b", marginTop: "6px", fontWeight: 500 }}>
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
          </div>
        ))}
      </div>
    </div>
  );
};

export default AdminDashboard;