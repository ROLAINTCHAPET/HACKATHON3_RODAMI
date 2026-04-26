/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Association } from '../models/Association';
import type { EventImpactDTO } from '../models/EventImpactDTO';
import type { GovernanceRule } from '../models/GovernanceRule';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class GovernanceControllerService {
    /**
     * @param key
     * @param requestBody
     * @returns GovernanceRule OK
     * @throws ApiError
     */
    public static updateRule(
        key: string,
        requestBody: Record<string, string>,
    ): CancelablePromise<GovernanceRule> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/governance/rules/{key}',
            path: {
                'key': key,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @param id
     * @param requestBody
     * @returns Association OK
     * @throws ApiError
     */
    public static validateAssociation(
        id: number,
        requestBody: Record<string, string>,
    ): CancelablePromise<Association> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/governance/associations/{id}/validate',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @param id
     * @returns EventImpactDTO OK
     * @throws ApiError
     */
    public static getEventImpact(
        id: number,
    ): CancelablePromise<EventImpactDTO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/governance/impact/events/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * @returns any OK
     * @throws ApiError
     */
    public static getAuditLogs(): CancelablePromise<any> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/governance/audit',
        });
    }
}
