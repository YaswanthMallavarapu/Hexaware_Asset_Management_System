import { CANCEL_RETURN_REQUEST, GET_ALL_ALLOCATIONS, REQUEST_ASSET_RETURN, SET_FILTER, SET_PAGE } from "../actions/asset-allocation-action"
import { UPDATE_ALLOCATION_SERVICE_STATUS } from "../actions/service-request-action"

const initialState = {
    allocations: [],
    totalPages: 1,
    page: 0,
    size: 5,
    filter: 'ALL',
    totalElements: 0
}

const AssetAllocationReducer = (state = initialState, action) => {

    switch (action.type) {
        case GET_ALL_ALLOCATIONS:
            // console.log(action);
            return {
                ...state,
                allocations: [...action.payload.list],
                totalPages: action.payload.totalPages,
                totalElements: action.payload.totalElements
            }
        case SET_FILTER:
            return {
                ...state,
                filter: action.payload,
                page: 0
            }


        case SET_PAGE:
            return {
                ...state,
                page: action.payload
            }

        case UPDATE_ALLOCATION_SERVICE_STATUS:
            let prev=state.allocations.find(a=>a.id===action.payload)
            prev={...prev,
                status:'SERVICE_REQUESTED'
            }
            const temp_service=[...state.allocations].filter(a=>a.id!==action.payload)
            return {
                ...state,
                allocations:[...temp_service,prev]
            }

        case REQUEST_ASSET_RETURN:


            const temp = [...state.allocations].filter(allocation => allocation.id != action.payload.id)
            const updated = action.payload.status = 'RETURN_REQUESTED'
            return {
                ...state,
                allocations: [...temp, updated]
            }


        case CANCEL_RETURN_REQUEST:
            const cancel_temp = [...state.allocations].filter(allocation => allocation.id != action.payload.id)
            const cancel_updated = action.payload.status = 'ALLOCATED'
            return {
                ...state,
                allocation: [...cancel_temp,cancel_updated]
            }


        default:
            return state
    }
}
export default AssetAllocationReducer