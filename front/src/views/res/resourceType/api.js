import { getRequest, postRequest, putRequest, postBodyRequest, getNoAuthRequest, postNoAuthRequest } from '@/libs/axios';

export const getResourceTypeOne = (params) => {
    return getRequest('/resourceType/getOne', params)
}
export const getResourceTypeList = (params) => {
    return getRequest('/resourceType/getByPage', params)
}
export const getResourceTypeCount = (params) => {
    return getRequest('/resourceType/count', params)
}
export const addResourceType = (params) => {
    return postRequest('/resourceType/insert', params)
}
export const editResourceType = (params) => {
    return postRequest('/resourceType/update', params)
}
export const addOrEditResourceType = (params) => {
    return postRequest('/resourceType/insertOrUpdate', params)
}
export const deleteResourceType = (params) => {
    return postRequest('/resourceType/delByIds', params)
}