import React, { useEffect, useState } from "react";
import axios from "axios";
import { BsCamera, BsPersonCircle } from "react-icons/bs";

const ManagerProfile = () => {
    const [manager, setManager] = useState(null);
    const [file, setFile] = useState(null);
    const [profile, setProfile] = useState(null);
    const [error, setError] = useState("");

    const api = "http://localhost:8082/api/admin/upload";
    const managerApi = "http://localhost:8082/api/admin/get-one";
    const profileApi = "http://localhost:8082/api/admin/profile";

    const config = {
        headers: {
            Authorization: "Bearer " + localStorage.getItem("token"),
        },
    };

    useEffect(() => {
        fetchManager();
        fetchProfile();
    }, []);

    const fetchManager = async () => {
        try {
            const res = await axios.get(managerApi, config);
            setManager(res.data);
        } catch (err) {
            setError("Failed to load admin details.");
        }
    };

    const fetchProfile = async () => {
        try {
            const res = await axios.get(profileApi, config);
            if (res.data) {
                setProfile(`/uploads/${res.data}`);
            }
        } catch (err) {
            console.error(err);
        }
    };

    const handleUpload = async () => {
        if (!file) return;

        try {
            const formData = new FormData();
            formData.append("file", file);

            const res = await axios.post(api, formData, config);
            setProfile(`/uploads/${res.data.profileImage}`);
            setFile(null);
        } catch (err) {
            setError("Upload failed.");
        }
    };

    return (
        <div
            className="container-fluid py-4 px-4"
            style={{ backgroundColor: "#f0f4f8", minHeight: "100vh" }}
        >
            {/* Header */}
            <div className="mb-4">
                <h5 className="fw-bold mb-0" style={{ color: "#1e293b" }}>
                    My Profile
                </h5>
                <p className="text-muted small mb-0">
                    Manage your personal information and profile picture
                </p>
            </div>

            {/* Error */}
            {error && (
                <div className="alert alert-danger py-2 px-3 mb-3">
                    {error}
                </div>
            )}

            {/* Profile Card */}
            <div
                className="card border-0"
                style={{
                    borderRadius: "16px",
                    boxShadow: "0 2px 12px rgba(0,0,0,0.07)",
                    maxWidth: "700px",
                }}
            >
                <div className="card-body p-4">
                    <div className="row align-items-center">

                        {/* Profile Image Section */}
                        <div className="col-md-4 text-center mb-3 mb-md-0">
                            <div
                                style={{
                                    width: "140px",
                                    height: "140px",
                                    borderRadius: "50%",
                                    overflow: "hidden",
                                    margin: "0 auto",
                                    backgroundColor: "#f1f5f9",
                                    display: "flex",
                                    alignItems: "center",
                                    justifyContent: "center",
                                }}
                            >
                                {profile ? (
                                    <img
                                        src={profile}
                                        alt="Profile"
                                        style={{
                                            width: "100%",
                                            height: "100%",
                                            objectFit: "cover",
                                        }}
                                    />
                                ) : (
                                    <BsPersonCircle size={80} color="#94a3b8" />
                                )}
                            </div>

                            <div className="mt-3">
                                <input
                                    type="file"
                                    className="form-control form-control-sm"
                                    onChange={(e) => setFile(e.target.files[0])}
                                />
                                <button
                                    onClick={handleUpload}
                                    disabled={!!profile}
                                    className="btn btn-sm mt-2 w-100"
                                    style={{
                                        backgroundColor: !!profile ? "#94a3b8" : "#2563eb",
                                        color: "#fff",
                                        borderRadius: "8px",
                                        fontWeight: 500,
                                        cursor: !!profile ? "not-allowed" : "pointer",
                                    }}
                                >
                                    <BsCamera size={14} className="me-1" />
                                    {profile ? "Profile Uploaded" : "Update Photo"}
                                </button>
                            </div>
                        </div>

                        {/* Details Section */}
                        <div className="col-md-8">
                            <div className="mb-3">
                                <label className="text-muted small">Full Name</label>
                                <div
                                    className="fw-semibold"
                                    style={{ fontSize: "1rem", color: "#1e293b" }}
                                >
                                    {manager?.name || "—"}
                                </div>
                            </div>

                            <div className="mb-3">
                                <label className="text-muted small">Username</label>
                                <div
                                    className="fw-semibold"
                                    style={{ fontSize: "0.95rem", color: "#475569" }}
                                >
                                    {manager?.username || "—"}
                                </div>
                            </div>

                            <div>
                                <label className="text-muted small">Role</label>
                                <div
                                    className="fw-semibold"
                                    style={{ fontSize: "0.95rem", color: "#475569" }}
                                >
                                    ADMIN
                                </div>
                            </div>
                        </div>

                    </div>
                </div>
            </div>
        </div>
    );
};

export default ManagerProfile;