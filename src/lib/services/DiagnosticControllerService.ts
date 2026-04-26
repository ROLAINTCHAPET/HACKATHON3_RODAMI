/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Connection } from '../models/Connection';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class DiagnosticControllerService {
    /**
     * @returns any OK
     * @throws ApiError
     */
    public static getTwist09Diagnostics(): CancelablePromise<Record<string, Record<string, any>>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/diagnostics/twist09',
        });
    }
    /**
     * @returns Connection OK
     * @throws ApiError
     */
    public static debugConnections(): CancelablePromise<Array<Connection>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/diagnostics/debug/connections',
        });
    }
}
