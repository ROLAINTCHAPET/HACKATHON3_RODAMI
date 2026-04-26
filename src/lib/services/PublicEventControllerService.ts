/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { EventResponse } from '../models/EventResponse';
import type { GuestRegistrationRequest } from '../models/GuestRegistrationRequest';
import type { RegistrationResponse } from '../models/RegistrationResponse';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class PublicEventControllerService {
    /**
     * @param token
     * @param requestBody
     * @returns RegistrationResponse OK
     * @throws ApiError
     */
    public static registerGuest(
        token: string,
        requestBody: GuestRegistrationRequest,
    ): CancelablePromise<RegistrationResponse> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/public/events/{token}/register',
            path: {
                'token': token,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @param token
     * @returns EventResponse OK
     * @throws ApiError
     */
    public static getByToken(
        token: string,
    ): CancelablePromise<EventResponse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/public/events/{token}',
            path: {
                'token': token,
            },
        });
    }
}
