/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { ActivityHistoryDTO } from '../models/ActivityHistoryDTO';
import type { AuthResponse } from '../models/AuthResponse';
import type { Interest } from '../models/Interest';
import type { InterestCatalogDTO } from '../models/InterestCatalogDTO';
import type { OnboardingRequest } from '../models/OnboardingRequest';
import type { ProfileDTO } from '../models/ProfileDTO';
import type { ProfileSetupRequest } from '../models/ProfileSetupRequest';
import type { UpdateProfileRequest } from '../models/UpdateProfileRequest';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class ProfileControllerService {
    /**
     * @param id
     * @returns ProfileDTO OK
     * @throws ApiError
     */
    public static getProfile(
        id: number,
    ): CancelablePromise<ProfileDTO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/profiles/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * @param id
     * @param requestBody
     * @returns ProfileDTO OK
     * @throws ApiError
     */
    public static updateProfile(
        id: number,
        requestBody: UpdateProfileRequest,
    ): CancelablePromise<ProfileDTO> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/profiles/{id}',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @param id
     * @returns Interest OK
     * @throws ApiError
     */
    public static getUserInterests(
        id: number,
    ): CancelablePromise<Array<Interest>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/profiles/{id}/interests',
            path: {
                'id': id,
            },
        });
    }
    /**
     * @param id
     * @param requestBody
     * @returns Interest Created
     * @throws ApiError
     */
    public static addInterests(
        id: number,
        requestBody: Array<string>,
    ): CancelablePromise<Array<Interest>> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/profiles/{id}/interests',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @param requestBody
     * @returns ProfileDTO OK
     * @throws ApiError
     */
    public static setupProfile(
        requestBody: ProfileSetupRequest,
    ): CancelablePromise<ProfileDTO> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/profiles/setup',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @param requestBody
     * @returns AuthResponse Created
     * @throws ApiError
     */
    public static onboard(
        requestBody: OnboardingRequest,
    ): CancelablePromise<AuthResponse> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/profiles/onboard',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @param id
     * @returns number OK
     * @throws ApiError
     */
    public static getCompletion(
        id: number,
    ): CancelablePromise<Record<string, number>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/profiles/{id}/completion',
            path: {
                'id': id,
            },
        });
    }
    /**
     * @returns ProfileDTO OK
     * @throws ApiError
     */
    public static getMyProfile(): CancelablePromise<ProfileDTO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/profiles/me',
        });
    }
    /**
     * @returns ActivityHistoryDTO OK
     * @throws ApiError
     */
    public static getMyHistory(): CancelablePromise<ActivityHistoryDTO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/profiles/me/history',
        });
    }
    /**
     * @returns any OK
     * @throws ApiError
     */
    public static getColdStartSuggestions(): CancelablePromise<Record<string, Record<string, any>>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/profiles/cold-start/suggestions',
        });
    }
    /**
     * @returns InterestCatalogDTO OK
     * @throws ApiError
     */
    public static getInterestCatalog(): CancelablePromise<Record<string, Array<InterestCatalogDTO>>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/interests/catalog',
        });
    }
    /**
     * @param id
     * @param tag
     * @returns void
     * @throws ApiError
     */
    public static removeInterest(
        id: number,
        tag: string,
    ): CancelablePromise<void> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/profiles/{id}/interests/{tag}',
            path: {
                'id': id,
                'tag': tag,
            },
        });
    }
}
