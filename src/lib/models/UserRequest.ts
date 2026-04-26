/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { InterestRequest } from './InterestRequest';
export type UserRequest = {
    nom: string;
    prenom: string;
    email: string;
    firebaseUid?: string;
    filiere?: string;
    annee?: number;
    statut?: string;
    interests?: Array<InterestRequest>;
};

