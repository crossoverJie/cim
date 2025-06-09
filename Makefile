IMAGE_NAME = allin1-ubuntu
REGISTRY = ghcr.io
OWNER = $(shell git config --get remote.origin.url | sed -e 's/.*github.com[:/]\([^/]*\)\/.*/\1/')
TAG_PREFIX = image

# make tag VERSION=v1.0
tag:
	$(if $(VERSION),,$(error VERSION variable not set. Usage: make tag VERSION=x.y.z))
	git tag -f $(TAG_PREFIX)-$(VERSION)
	git push origin $(TAG_PREFIX)-$(VERSION)

# list all tags with the prefix
list-tags:
	git tag -l "$(TAG_PREFIX)-*" | sort -V

# get the latest tag
get-latest:
	@git describe --abbrev=0 --tags --match="$(TAG_PREFIX)-*" 2>/dev/null | sed 's/$(TAG_PREFIX)-//' || echo "No tags found"

# make version TYPE=major|minor|patch
version:
	$(if $(TYPE),,$(error TYPE variable not set. Options: major, minor, patch))
	$(eval CURRENT := $(shell make get-latest))
	$(if $(CURRENT),,$(error No existing tags found. Create first tag with: make tag VERSION=1.0.0))

	$(eval MAJOR := $(shell echo $(CURRENT) | cut -d. -f1))
	$(eval MINOR := $(shell echo $(CURRENT) | cut -d. -f2))
	$(eval PATCH := $(shell echo $(CURRENT) | cut -d. -f3))

	$(if $(filter $(TYPE),major),$(eval NEW_VERSION := $(($(MAJOR)+1)).0.0))
	$(if $(filter $(TYPE),minor),$(eval NEW_VERSION := $(MAJOR).$(($(MINOR)+1)).0))
	$(if $(filter $(TYPE),patch),$(eval NEW_VERSION := $(MAJOR).$(MINOR).$(($(PATCH)+1))))

	make tag VERSION=$(NEW_VERSION)

.PHONY: tag list-tags get-latest version