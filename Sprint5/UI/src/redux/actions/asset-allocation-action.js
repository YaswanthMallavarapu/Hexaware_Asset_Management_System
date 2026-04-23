import axios from "axios"

export const GET_ALL_ALLOCATIONS = "GET_ALL_ALLOCATIONS"
export const SET_FILTER = "SET_FILTER"
export const SET_PAGE = 'SET_PAGE'
export const REQUEST_ASSET_RETURN = 'REQUEST_ASSET_RETURN'
export const CANCEL_RETURN_REQUEST='CANCEL_RETURN_REQUEST'


export const getAllAllocations = (page = 0, size = 5, filter = "ALL") => {
    return async (dispatch) => {



        const config = {
            headers: {
                Authorization: "Bearer " + localStorage.getItem("token")
            }
        }

        let response = undefined
        if (filter === 'ALL') {
            response = await axios.get(`http://localhost:8082/api/asset-allocation/get-all/allocated?page=${page}&size=${size}`, config)
        } else {
            response = await axios.get(`http://localhost:8082/api/asset-allocation/get/user/status/${filter}`, config)
        }

        dispatch({
            type: GET_ALL_ALLOCATIONS,
            payload: response.data
        })

    }

}

export const setFilter = (filter) => {

    return (dispatch) => {
        dispatch({
            type: SET_FILTER,
            payload: filter
        })
    }
}

export const setPage = (page) => {

    return (dispatch) => {

        dispatch({
            type: SET_PAGE,
            payload: page
        })
    }
}

export const returnAssetRequest = (assetAllocation) => {
    const config = {
        headers: {
            Authorization: "Bearer " + localStorage.getItem("token")
        }
    }

    return async (dispatch) => {


        const response = axios.put(`http://localhost:8082/api/asset-allocation/return-asset-request/${assetAllocation.id}`, {}, config)

        dispatch({
            type: REQUEST_ASSET_RETURN,
            payload: assetAllocation
        })

    }
}

export const cancelReturnRequest = (assetAllocation) => {

    return async (dispatch) => {

        const config = {
            headers: {
                Authorization: "Bearer " + localStorage.getItem("token")
            }
        }

        axios.put(`http://localhost:8082/api/asset-allocation/cancel-return-asset-request/${assetAllocation.id}`,{},config)

        dispatch({
            type:CANCEL_RETURN_REQUEST,
            payload:assetAllocation
        })

    }
}

