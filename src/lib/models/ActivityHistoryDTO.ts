/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { ConnectionHistoryDTO } from './ConnectionHistoryDTO';
import type { EventHistoryDTO } from './EventHistoryDTO';
export type ActivityHistoryDTO = {
    recentEvents?: Array<EventHistoryDTO>;
    recentConnections?: Array<ConnectionHistoryDTO>;
};

