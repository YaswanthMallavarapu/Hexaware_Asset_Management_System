import React, { useEffect, useState } from "react";
import { Outlet, useNavigate } from "react-router-dom";
import Sidebar from "./admin-sidebar";
import axios from "axios";


const AdminLayout = () => {
  const [isOpen, setIsOpen] = useState(true);
  const navigate =useNavigate()

  const [admin,setAdmin]=useState(undefined)
  // have to implement this today

  const adminApi="http://localhost:8082/api/admin/get-one"
  const config={
    headers:{
      Authorization:"Bearer "+localStorage.getItem("token")
    }
  }

  useEffect(()=>{

    const fetchUser=async ()=>{

      try {
        const response=await axios.get(adminApi,config)
        setAdmin(response.data)
        console.log(response.data);

        
      } catch (error) {
        navigate("/login")
      }

    }
    fetchUser()

  },[])

  return (
    <div className="d-flex" style={{ minHeight: "100vh" }}>
      
      <Sidebar isOpen={isOpen} setIsOpen={setIsOpen} />

      {/* Content automatically adjusts */}
      <div className="flex-grow-1 p-4 bg-light">
        <Outlet />
      </div>

    </div>
  );
};

export default AdminLayout;