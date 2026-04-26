/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { EventCategory } from '../models/EventCategory';
import type { EventRequest } from '../models/EventRequest';
import type { EventResponse } from '../models/EventResponse';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class VNementsService {
    /**
     * Lister les catégories
     * Retourne toutes les catégories d'événements triées par priorité BDE décroissante.
     * @returns EventCategory Liste des catégories
     * @throws ApiError
     */
    public static getCategories(): CancelablePromise<Array<EventCategory>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/categories',
        });
    }
    /**
     * Lister les événements publiés
     * Retourne tous les événements publiés, triés par **priorité BDE** (décroissant) puis par date.
     *
     * **Filtres disponibles (query params) :**
     * - `?keyword=machine-learning` → recherche dans le titre et la description
     * - `?categoryId=3` → filtre par catégorie
     * - `?upcoming` → uniquement les événements à venir
     *
     * @param keyword Mot-clé de recherche
     * @param categoryId ID de catégorie
     * @param upcoming Événements à venir uniquement
     * @returns EventResponse Liste des événements
     * @throws ApiError
     */
    public static getAllEvents(
        keyword?: any,
        categoryId?: any,
        upcoming?: any,
    ): CancelablePromise<Array<EventResponse>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/events',
            query: {
                'keyword': keyword,
                'categoryId': categoryId,
                'upcoming': upcoming,
            },
        });
    }
    /**
     * Créer un événement 🔒 BDE/Admin
     * Crée un nouvel événement en statut **DRAFT**.
     * L'événement doit ensuite être publié via `PATCH /api/events/{id}/publish`.
     *
     * > Nécessite le rôle `BDE` ou `ADMIN`.
     *
     * @param requestBody
     * @returns EventResponse Événement créé (DRAFT)
     * @throws ApiError
     */
    public static createEvent(
        requestBody: EventRequest,
    ): CancelablePromise<EventResponse> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/events',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                400: `Données invalides ou catégorie introuvable`,
                403: `Rôle insuffisant`,
            },
        });
    }
    /**
     * Détail d'un événement
     * Retourne le détail enrichi d'un événement (catégorie, organisateur, nombre de participants).
     * @param id ID de l'événement
     * @returns EventResponse Événement trouvé
     * @throws ApiError
     */
    public static getEvent(
        id: any,
    ): CancelablePromise<EventResponse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/events/{id}',
            path: {
                'id': id,
            },
            errors: {
                404: `Événement introuvable`,
            },
        });
    }
    /**
     * Modifier un événement 🔒 BDE/Admin
     * @param id
     * @param requestBody
     * @returns EventResponse Événement mis à jour
     * @throws ApiError
     */
    public static updateEvent(
        id: any,
        requestBody: EventRequest,
    ): CancelablePromise<EventResponse> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/events/{id}',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                404: `Événement introuvable`,
            },
        });
    }
    /**
     * Supprimer un événement 🔒 BDE/Admin
     * @param id
     * @returns void
     * @throws ApiError
     */
    public static deleteEvent(
        id: any,
    ): CancelablePromise<void> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/events/{id}',
            path: {
                'id': id,
            },
            errors: {
                404: `Événement introuvable`,
            },
        });
    }
    /**
     * Annuler un événement 🔒 BDE/Admin
     * Passe le statut à **CANCELLED**. L'événement disparaît du flux public.
     * @param id
     * @returns EventResponse Événement annulé
     * @throws ApiError
     */
    public static cancelEvent(
        id: any,
    ): CancelablePromise<EventResponse> {
        return __request(OpenAPI, {
            method: 'PATCH',
            url: '/api/events/{id}/cancel',
            path: {
                'id': id,
            },
        });
    }
    /**
     * Publier un événement 🔒 BDE/Admin
     * Passe le statut de DRAFT à **PUBLISHED**. L'événement devient visible dans le flux.
     * @param id
     * @returns EventResponse Événement publié
     * @throws ApiError
     */
    public static publishEvent(
        id: any,
    ): CancelablePromise<EventResponse> {
        return __request(OpenAPI, {
            method: 'PATCH',
            url: '/api/events/{id}/publish',
            path: {
                'id': id,
            },
            errors: {
                404: `Événement introuvable`,
            },
        });
    }
}
