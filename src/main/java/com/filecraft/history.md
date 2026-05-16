15.05.2026
fix: resolve PostgreSQL schema and UUID issues

- fixed UUID generation strategy for Workspace entity
- added explicit workspaces table mapping
- added automatic createdAt / updatedAt handling
- removed old conflicting PostgreSQL tables
- recreated clean database schema
- fixed workspace vs workspaces naming conflict
- connected to correct PostgreSQL database (filecraft_db)
- verified Hibernate table generation
- fixed FileAssetRepository return type
- added proper sha256_hash column mapping
- verified file_assets table structure
- successfully tested workspace creation with Postman