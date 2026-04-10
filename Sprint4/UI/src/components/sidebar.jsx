import React from "react";

const Sidebar = () => {
  return (
    <div className="container-fluid">
      <div className="row">
        
        {/* Sidebar */}
        <div className="col-md-2 bg-light min-vh-100">
          <div className="p-3">
            <h5>Dashboard</h5>
            <h5>Admins</h5>
            <h5>Managers</h5>
            <h5>Employees</h5>
            <h5>Asset Categories</h5>
            <h5>Assets</h5>
            <h5>Asset Requests</h5>
            <h5>Asset Allocations</h5>
            <h5>Service Request</h5>
            <h5>Audits</h5>
          </div>
        </div>

      </div>
    </div>
  );
};

export default Sidebar;