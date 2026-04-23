import axios from "axios"
export const DELETE_SERVICE_REQUEST = 'DELETE_SERVICE_REQUEST'
export const ADD_SERVICE_REQUEST = 'ADD_SERVICE_REQUEST'
export const UPDATE_ALLOCATION_SERVICE_STATUS = 'UPDATE_ALLOCATION_SERVICE_STATUS'
export const GET_ALL_SERVICE_REQUESTS = 'GET_ALL_SERVICE_REQUESTS'
export const SET_PAGE='SET_PAGE'
export const SET_FILTER='SET_FILTER'

export const getAllServiceRequests = (page = 0, size = 5, filter = 'ALL') => {

    const config = {
        headers: {
            Authorization: "Bearer " + localStorage.getItem("token")
        }
    }

    return async (dispatch) => {
        let response = undefined

        if (filter == 'ALL') {
            response = await axios.get(`http://localhost:8082/api/service-request/user/get-all?page=${page}&size=${size}`, config)
        }
        else {

            response = await axios.get(`http://localhost:8082/api/service-request/user/status/${filter}?page=${page}&size=${size}`, config)
        }

        dispatch({
            type: GET_ALL_SERVICE_REQUESTS,
            payload: response.data
        })
    }

}

export const deleteServiceRequest = (serviceRequestId) => {

    const config = {
        headers: {
            Authorization: "Bearer " + localStorage.getItem("token")
        }
    }
    return async (dispatch) => {

        await axios.delete(`http://localhost:8082/api/service-request/delete/${serviceRequestId}`, config)
        dispatch({
            type: DELETE_SERVICE_REQUEST,
            payload: serviceRequestId
        })
    }
}

export const addServiceRequest = (details, assetAllocationId) => {
    const config = {
        headers: {
            Authorization: "Bearer " + localStorage.getItem("token")
        }
    }

    return async (dispatch) => {

        const response = await axios.post(`http://localhost:8082/api/service-request/request-service/${assetAllocationId}`, {
            description: details
        }, config)

        const request = {
            description: details,
            assetAllocationId: assetAllocationId
        }

        dispatch({
            type: UPDATE_ALLOCATION_SERVICE_STATUS,
            payload: assetAllocationId
        })
        dispatch({
            type: ADD_SERVICE_REQUEST,
            payload: request
        })

    }
}

export const setPage=(page)=>{

    return (dispatch)=>{
        dispatch({
            type:SET_PAGE,
            payload:page
        })
    }
}

export const setFilter=(filter)=>{

    return (dispatch)=>{
        dispatch({
            type:SET_FILTER,
            payload:filter
        })
    }
}