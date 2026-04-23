import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  deleteServiceRequest,
  getAllServiceRequests,
  setFilter,
  setPage,
} from "../../redux/actions/service-request-action";
import { BsChevronLeft, BsChevronRight, BsFunnel } from "react-icons/bs";
import "../../styles/employee-service-request.css";

const EmployeeServiceRequest = () => {
  const dispatch = useDispatch();

  const { requests, totalPages, filter, page, size } = useSelector(
    (state) => state.serviceRequestReducer
  );

  useEffect(() => {
    dispatch(getAllServiceRequests(page, size, filter));
  }, [dispatch, page, filter]);

  const filters = [
    { name: "All", value: "ALL" },
    { name: "Open", value: "OPEN" },
    { name: "In Progress", value: "IN_PROGRESS" },
    { name: "Resolved", value: "RESOLVED" },
    { name: "Rejected", value: "REJECTED" },
  ];

  const hasPrev = () => page > 0;
  const hasNext = () => page < totalPages - 1;

  const formatDate = (date) => {
    if (!date) return "—";
    return new Date(date).toLocaleDateString("en-IN", {
      day: "2-digit",
      month: "short",
      year: "numeric",
    });
  };

  return (
    <div className="container-fluid py-4 px-4 employee-allocations-wrapper">

      {/* Header */}
      <div className="mb-4">
        <h5 className="fw-bold mb-0">My Service Requests</h5>
        <p className="text-muted small mb-0">
          Track and monitor your service requests
        </p>
      </div>

      <div className="d-flex align-items-center gap-3 mb-4 flex-wrap">
        <BsFunnel size={14} className="text-muted" />

        {filters.map((f) => (
          <button
            key={f.value}
            onClick={() => {
              dispatch(setFilter(f.value));
              dispatch(setPage(0));
            }}
            className={`filter-btn ${filter === f.value ? "active-filter" : ""
              }`}
          >
            {f.name}
          </button>
        ))}
      </div>

      {/* Card */}
      <div className="card allocation-card">
        <div className="card-body p-0">

          <table className="table mb-0">
            <thead>
              <tr>
                <th>#</th>
                <th>Asset</th>
                <th>Description</th>
                <th>Manager</th>
                <th>Requested Date</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {requests.length === 0 ? (
                <tr>
                  <td colSpan="7" className="text-center py-5 text-muted">
                    No service requests found
                  </td>
                </tr>
              ) : (
                requests.map((request, index) => (
                  <tr key={request.id || index}>
                    <td>{page * size + index + 1}</td>
                    <td className="fw-semibold">{request.assetName}</td>
                    <td>{request.description}</td>
                    <td>{request.managerName}</td>
                    <td>{formatDate(request.requestDate)}</td>
                    <td>
                      <span
                        className={`status-badge status-${request.status?.toLowerCase()}`}
                      >
                        {request.status}
                      </span>
                    </td>
                    <td>
                      {
                        request.status === 'OPEN' &&
                        <div>
                          <button
                            className="btn btn-outline-danger btn-sm action-btn"
                            onClick={() => dispatch(deleteServiceRequest(request.id))}
                          >
                            Cancel
                          </button>
                        </div>
                      }
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>

        </div>

        {/* Pagination (UNCHANGED LOGIC) */}
        <div className="d-flex justify-content-between align-items-center px-4 py-3 border-top">
          <span className="text-muted small">
            Page {page + 1} of {totalPages}
          </span>

          <div className="d-flex gap-2">
            <button
              className="pagination-btn"
              disabled={!hasPrev()}
              onClick={() => hasPrev() && dispatch(setPage(page - 1))}
            >
              <BsChevronLeft size={14} /> Prev
            </button>

            <button
              className="pagination-btn"
              disabled={!hasNext()}
              onClick={() => hasNext() && dispatch(setPage(page + 1))}
            >
              Next <BsChevronRight size={14} />
            </button>
          </div>
        </div>

      </div>

    </div>
  );
};

export default EmployeeServiceRequest;