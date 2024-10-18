# 24Fall-ASE-HakunaMatata

## Endpoints
#### GET /checkAnno
* Checks if a route and corresponding annotation exist based on the provided routeId and userId.
* Requires routeId and userId parameters.
* Returns information on whether the annotation exists.
* Upon Success: Returns HTTP 200 status indicating the annotation exists.
* Upon Failure: Returns HTTP 404 status indicating either the route or annotation is missing.
#### PATCH /editRoute
* Edits or creates a new annotation document based on the provided routeId and userId.
* Requires routeId, userId, and stopList parameters.
* Returns success or error messages.
* Upon Success: Returns HTTP 200 status indicating the annotation has been successfully updated or created.
* Upon Failure: Returns HTTP 400 status if an error occurs during the operation.
#### DELETE /deleteAnno
* Deletes an annotation document based on the provided routeId and userId.
* Requires routeId and userId parameters.
* Returns information on the deletion result.
* Upon Success: Returns HTTP 200 status indicating the annotation has been successfully deleted.
* Upon Failure: Returns HTTP 404 status indicating the annotation was not found, or HTTP 400 status indicating an error occurred during the operation.
