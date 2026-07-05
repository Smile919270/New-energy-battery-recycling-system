import { getRequest, postRequest, putRequest, postBodyRequest, getNoAuthRequest, postNoAuthRequest } from '@/libs/axios';

export const getResourceOrganizationOne = (params) => {
    return getRequest('/resourceOrganization/getOne', params)
}
export const getResourceOrganizationList = (params) => {
    return getRequest('/resourceOrganization/getByPage', params)
}
export const getResourceOrganizationCount = (params) => {
    return getRequest('/resourceOrganization/count', params)
}
export const addResourceOrganization = (params) => {
    return postRequest('/resourceOrganization/insert', params)
}
export const editResourceOrganization = (params) => {
    return postRequest('/resourceOrganization/update', params)
}
export const addOrEditResourceOrganization = (params) => {
    return postRequest('/resourceOrganization/insertOrUpdate', params)
}
export const deleteResourceOrganization = (params) => {
    return postRequest('/resourceOrganization/delByIds', params)
}