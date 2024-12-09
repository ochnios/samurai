export interface MessageSource {
  id: string;
  documentId?: string;
  originalTitle: string;
  retrievedContent: string;
  updated: boolean;
  deleted: boolean;
}
