import React from "react";

const Managers = () => {

  const managers = [
    {
      name: "Arjun Reddy",
      email: "arjun.reddy@company.com",
      role: "Engineering Manager",
      team: "Backend Team Lead",
      status: "Active"
    },
    {
      name: "Priya Sharma",
      email: "priya.sharma@company.com",
      role: "Product Manager",
      team: "Product Strategy Lead",
      status: "Active"
    },
    {
      name: "Rahul Verma",
      email: "rahul.verma@company.com",
      role: "Operations Manager",
      team: "Process Improvement Lead",
      status: "On Leave"
    },
    {
      name: "Ananya Iyer",
      email: "ananya.iyer@company.com",
      role: "HR Manager",
      team: "Talent Acquisition Lead",
      status: "Active"
    }
  ];

  return (
    <div className="container mt-4">

      {/* Header */}
      <div className="d-flex justify-content-between align-items-center mb-3">
        <div>
          <h4 className="mb-0">Recent Managers</h4>
          <small className="text-muted">Manage your management team</small>
        </div>
        
      </div>

      {/* Manager List */}
      {managers.map((manager, index) => (
        <div key={index} className="border rounded p-3 mb-3 bg-white">
          <div className="row">
            
            <div className="col-md-4">
              <strong>{manager.name}</strong><br />
              <small className="text-muted">{manager.email}</small>
            </div>

            <div className="col-md-3">
              {manager.role}
            </div>

            <div className="col-md-3">
              {manager.team}
            </div>

            <div className="col-md-1">
              <span className={
                manager.status === "Active"
                  ? "text-success"
                  : "text-warning"
              }>
                {manager.status}
              </span>
            </div>

            <div className="col-md-1 text-end">
              ...
            </div>

          </div>
        </div>
      ))}

    </div>
  );
};

export default Managers;