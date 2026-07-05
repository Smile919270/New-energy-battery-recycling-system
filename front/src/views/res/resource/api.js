import { getRequest, postRequest, putRequest, postBodyRequest, getNoAuthRequest, postNoAuthRequest } from '@/libs/axios';

export const getResourceOne = (params) => {
    return getRequest('/resource/getOne', params)
}
export const getResourceList = (params) => {
    return getRequest('/resource/getByPage', params)
}
export const getResourceCount = (params) => {
    return getRequest('/resource/count', params)
}
export const addResource = (params) => {
    return postRequest('/resource/insert', params)
}
export const editResource = (params) => {
    return postRequest('/resource/update', params)
}
export const addOrEditResource = (params) => {
    return postRequest('/resource/insertOrUpdate', params)
}
export const deleteResource = (params) => {
    return postRequest('/resource/delByIds', params)
}
export const getResourceTypeList = (params) => {
    return getRequest('/resourceType/getAll', params)
}