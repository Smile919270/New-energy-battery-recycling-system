import { getRequest, postRequest, putRequest, postBodyRequest, getNoAuthRequest, postNoAuthRequest } from '@/libs/axios';

export const getReceptionistOne = (params) => {
    return getRequest('/receptionist/getOne', params)
}
export const getReceptionistList = (params) => {
    return getRequest('/receptionist/getByPage', params)
}
export const getReceptionistCount = (params) => {
    return getRequest('/receptionist/count', params)
}
export const addReceptionist = (params) => {
    return postRequest('/receptionist/insert', params)
}
export const editReceptionist = (params) => {
    return postRequest('/receptionist/update', params)
}
export const addOrEditReceptionist = (params) => {
    return postRequest('/receptionist/insertOrUpdate', params)
}
export const deleteReceptionist = (params) => {
    return postRequest('/receptionist/delByIds', params)
}